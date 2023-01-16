# 📄 G사 주주총회 전자 투표 시스템

### 안건을 관리하고 투표를 수행할 수 있는 API 서버

<br/>

---

## 사용한 프레임워크 및 라이브러리

<br/><br/>

<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">

- ### SpringBoot(3.0.1) + Java(17)
    - _Spring Web_
    - _Spring Data JPA_
    - _Spring Security_
    - _Spring validation_
    - _Lombok_


- ### MySQL(DB)

<br/>

---

## 문서

<br/><br/>

### API 명세

- https://documenter.getpostman.com/view/24934524/2s8ZDSckEp

### ERD

- https://www.erdcloud.com/p/yjKBzyxfyn5MxQRfj

### 회고

- https://learned-kumquat-0dc.notion.site/1-2cfa7c10fe3c487481582bdd7cfb1bef
- https://learned-kumquat-0dc.notion.site/2-b87b26761db34bb28c0a4e693d495364
- https://learned-kumquat-0dc.notion.site/3-b9994eb8dbef459095553fa52d224e50
- https://learned-kumquat-0dc.notion.site/4-f8b06cc1ecf74badb4bb63023cc24257

<br/>

---

## 동작 방식

<br/><br/>

### Rest API 서버로서 Http Method 와 URL 을 통해 서버에게 요청, JSON 형태의 응답 수신

<br/>

![server2](https://user-images.githubusercontent.com/55526071/212576203-dae86dc1-bb0f-410e-878a-dc1f5ebf135e.PNG)

### 서버 내 컨트롤러, 서비스, 리포지토리 분리
- **Controller:** &nbsp;&nbsp; 웹 MVC의 컨트롤러 역할(REST controller) + 클라이언트에게 전달할 DTO로의 변환
- **Service:** &nbsp;&nbsp;도메인을 다루는 비즈니스 로직
- **Repository:** &nbsp;&nbsp; 데이터 베이스에 접근하여 도메인 데이터를 가져오고, 저장함
- **Domain:** &nbsp;&nbsp; 안건, 사용자, 투표 도메인 객체

<br/>

---

## 요구 사항 구현 방식

<br/><br/>

**_1. 시스템은 인증을 통해 인가된 사용자만 접근할 수 있어야 하고,    
주주와 관리자가 역할을 기반으로 할 수 있는 행동이 달라져야 한다_**

```java
http
        .requestMatchers("/api/sign-up").permitAll()
        .requestMatchers("/api/login").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/agendas").hasRole(Role.ADMIN.name())
        .requestMatchers(HttpMethod.DELETE, "/api/agendas/**").hasRole(Role.ADMIN.name())
        .requestMatchers("/api/agendas/*/terminate").hasRole(Role.ADMIN.name())
        .requestMatchers(HttpMethod.POST, "/api/votes").hasRole(Role.USER.name())
        .anyRequest().authenticated()
```
위 시큐리티 설정을 통해 인증되지 않은 사용자는 회원 가입과 로그인 URL에만 요청을 보낼 수 있으며,  
인증된 사용자도 인가되지 않은 URL에는 접근 불허

<br/><br/>


**_2. 시스템은 안건이라고 불리는 현안에 대해 찬성, 반대 또는 기권 의사를 표명할 수 있는 투표 기능을 제공해야 한다.    
안건은 관리자가 생성하거나 삭제할 수 있다_**

```java
@ResponseStatus(HttpStatus.CREATED)
@PostMapping("/agendas")
public SimpleAgendaResponseDto create(@RequestBody @Valid AgendaCreateRequestDto agendaCreateRequestDto) {
    Agenda savedAgenda = agendaService.createAgenda(agendaCreateRequestDto);
    return AgendaResponseFactory.getDto(Role.ADMIN, savedAgenda);
}

@DeleteMapping("/agendas/{id}")
public void delete(@PathVariable("id") Long agendaId) {
    agendaService.removeAgenda(agendaId);
}

@PostMapping("/votes")
public SimpleAgendaResponseDto vote(@RequestBody @Valid VoteRequestDto voteRequestDto) {
    Agenda agenda = agendaService.vote(
        SecurityUtil.getCurrentUserId(),
        voteRequestDto.getAgendaId(),
        voteRequestDto.getType(),
        voteRequestDto.getQuantity());
    return AgendaResponseFactory.getDto(Role.USER, agenda);
}
```
안건의 생성, 삭제 및 투표 기능은 위 컨트롤러를 통해 요청 및 수행할 수 있음

```java
@Transactional
public Agenda createAgenda(AgendaCreateRequestDto agendaCreateRequestDto) {
    return agendaRepository.save(Agenda.of(agendaCreateRequestDto));
}

@Transactional
public void removeAgenda(long agendaId) {
    agendaRepository.delete(agendaRepository.findById(agendaId).orElseThrow());
}

@Transactional
public Agenda vote(String userId, Long agendaID, VoteType type, int quantity) {
    User user = userService.getUser(userId);
    Agenda agenda = agendaRepository.findByIdWithLock(agendaID).orElseThrow();

    VotingSystem votingSystem = votingSystemFactory.makeVotingSystem(agenda);
    votingSystem.vote(user, agenda, type, quantity);
    return agenda;
}
```
컨트롤러에서 서비스로 로직 수행을 위임하고, 서비스에선 JPA Repository를 통해 안건을 생성, 삭제함  
투표 또한 안건의 투표 방식에 따른 투표 시스템을 통해 투표를 수행할 수 있음

<br/><br/>

**_3. 시스템은 사용자들에게 안건 목록을 조회할 수 있는 API를 제공해야 하고,  
해당 안건이 현재 투표 중인지 여부와 아직 진행되지 않은 경우 다음 투표 일정을 확인할 수 있어야 한다_**

```java
@GetMapping("/agendas")
public Page<AllAgendaResponseDto> agendas(
        @PageableDefault(sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {
    return agendaService.getAgendas(pageable).map(AllAgendaResponseDto::from);
}
```
안건 목록을 조회할 경우 페이징 된 안건 목록을 받을 수 있으며,  
클라이언트가 원하는 정렬 방식과 방향을 기준으로 정렬할 수 있음 (투표 시작 시각 빠른 순 기본)

또한 투표 별 ID 및 제목, 투표 가능 시간을 확인할 수 있음 (투표 시작 시각 ~ 투표 종료 시각)

<br/><br/>

**_4. 투표는 관리자가 게시하거나 종료할 수 있다.    
투표는 관리자가 직접 종료할 수도 있지만 투표를 게시하는 시점에 종료 시간을 통보하여 시스템이 해당 시간이 지난 후에 투표를 종료 시킬 수 있어야 한다_**

```java
@Column(nullable = false)
private LocalDateTime startsAt;

@Setter
@Column(nullable = false)
private LocalDateTime endsAt;
```
안건는 `진행중` 혹은 `종료` 와 같은 상태를 가지고 있지 않고,  
투표 혹은 조회를 요청한 시점과 지정한 종료 시각을 비교하여 유효성 및 종료 여부를 결정함

```java
@PatchMapping("/agendas/{id}/terminate")
public SimpleAgendaResponseDto end(@PathVariable("id") Long agendaId) {
    Agenda agenda = agendaService.terminate(agendaId);
    return AgendaResponseFactory.getDto(Role.ADMIN, agenda);
}

@Transactional
public Agenda terminate(long agendaId) {
    Agenda agenda = agendaRepository.findById(agendaId).orElseThrow();
    agenda.setEndsAt(LocalDateTime.now());
    return agenda;
}
```
때문에 종료 요청은 해당 안건의 종료 시각을 종료를 요청한 시각으로 변경하는 방식으로 동작

<br/><br/>

**_5. 의결권은 안건에 투표할 수 있는 투표권의 개수로 한 명의 주주는 여러 개의 의결권을 가질 수 있다_**

```java
private Integer voteRights;
```
사용자 엔티티 내 코드로, 사용자는 지정된 개수만큼의 의결권을 가질 수 있으며,   
사용자 생성 시 지정되는 값

<br/><br/>

**_6. 진행 중인 투표에 의결권을 행사할 때, 주주는 보유한 의결권보다 적게 행사할 수 있다_**

```java
private void validateUser(User user, int quantity) {
    if (user.getVoteRights() < quantity) {
        throw new InvalidVoteException(UserErrorCode.EXCEED_VOTE);
    }
}
```
투표 시스템의 구현체 코드 내 유효성을 검증하는 과정 중,  
사용자가 가진 의결권의 개수와 행사하려는 의결권의 개수를 비교하여 유효한지 검사하는 부분이  
포함되어 있음

<br/><br/>

**_7. 안건은 경영진의 요구에 따라 총 2 가지 투표 방식을 지원해야 한다. 첫 번째는 의결권 선착순 제한 경쟁이고 나머지는 제한이 없는 방식이다_**

```java
public enum AgendaType {

    NORMAL,
    LIMITED
}
```
투표 방식(일반, 선착순)을 Enum 으로 정의하고 안건 엔티티에 포함시켰음

<br/><br/>

**_8. 의결권 선착순 제한 경쟁은 투표에 참여하는 선착순으로 10개의 의결권만 투표에 반영하는 방식이다.    
예를 들면 A 주주는 3개의 의결권이 있고, B 주주는 8개의 의결권이 있을 때,   
A와 B가 순서대로 투표에 참여한다면 A는 3개의 의결권을 모두 행사할 수 있고, B는 8개 중 7개의 의결권만 행사할 수 있다.    
이후에 참가한 주주는 의결권 행사가 불가능하다_**

```java
@Override
public void vote(User user, Agenda agenda, VoteType type, int quantity) {
    quantity = Math.min(quantity, 10 - agenda.getTotalRights());
    super.vote(user, agenda, type, quantity);
    if (agenda.getTotalRights() >= 10) {
        agenda.setEndsAt(LocalDateTime.now());
    }
}
```
위는 선착순 투표 시스템의 투표 수행 메서드로,  
10개가 넘는 투표 요청이 들어올 경우, 10개까지만 반영되는 개수의 의결권만 받아들이도록 설계하였음

또한, 선착순 투표에 반영된 표가 10표가 되었을 때, 투표가 종료되도록 구현

<br/><br/>

**_9. 제한 없는 방식은 의결권의 제한 없이 모든 주주가 자신이 가진 모든 의결권을 안건에 투표할 수 있다_**

```java
private void validateUser(User user, int quantity) {
    if (user.getVoteRights() < quantity) {
        throw new InvalidVoteException(UserErrorCode.EXCEED_VOTE);
    }
}
```
요구사항 6번과 동일한 방식으로, 일반 투표의 경우 사용자가 소지한 의결권에 초과되지 않으면 유효한 투표로 인정됨

<br/><br/>

**_10. 시스템은 투표 결과를 투명하게 확인할 수 있도록 투표가 완료된 안건에 대해 그 목록과 찬성, 반대, 기권의 숫자를 확인할 수 있는 API를 제공해야 한다.    
관리자는 해당 API를 통해 어떤 사용자가 해당 안건에 찬성, 반대, 기권 의사 표명을 했는지 여부와 얼마나 많은 의결권을 행사했는지 확인할 수 있어야 한다_**

```java
public class AgendaResponseFactory {

  public static SimpleAgendaResponseDto getDto(Role role, Agenda agenda) {
    if (LocalDateTime.now().isAfter(agenda.getEndsAt())) {
      return new ResultAgendaResponseDto().from(agenda);
    }
    return switch (role) {
      case USER -> new SimpleAgendaResponseDto().from(agenda);
      case ADMIN -> new ResultAgendaResponseDto().from(agenda);
    };
  }

  public static SimpleAgendaResponseDto getDto(Agenda agenda, List<Vote> votes) {
    return new DetailResultAgendaResponse(votes).from(agenda);
  }
}
```
단일 안건 조회는 조건에 따라 크게 3가지로 분류할 수 있음

- **투표가 진행중일 경우**
  - 사용자: `안건 ID, 안건 제목, 투표 가능 시간`
  - 관리자: `안건 ID, 안건 제목, 투표 가능 시간, 현재 표 종류 별 개수(찬성, 반대, 무효)`
- **투표가 종료되었을 경우**
  - 사용자: `안건 ID, 안건 제목, 투표 가능 시간, 결과 표 종류 별 개수(찬성, 반대, 무효)`
  - 관리자: `안건 ID, 안건 제목, 투표 가능 시간, 결과 표 종류 별 개수(찬성, 반대, 무효), 사용자 별 투표 기록`

각 응답에 중복되는 부분이 있으므로 상속을 활용하여 각 응답을 구현하였음

```java
public class ResultAgendaResponseDto extends SimpleAgendaResponseDto {

    @JsonProperty
    private int positiveRights;

    @JsonProperty
    private int negativeRights;

    @JsonProperty
    private int invalidRights;

    public void setFrom(Agenda agenda) {
        super.setFrom(agenda);
        this.positiveRights = agenda.getPositiveRights();
        this.negativeRights = agenda.getNegativeRights();
        this.invalidRights = agenda.getInvalidRights();
    }

    @Override
    public ResultAgendaResponseDto from(Agenda agenda) {
        setFrom(agenda);
        return this;
    }
}
```

```java
@RequiredArgsConstructor
public class DetailResultAgendaResponse extends ResultAgendaResponseDto {

    private final List<Vote> votes;

    @JsonProperty
    private List<VoteResponseDto> voteResults;

    @Override
    public DetailResultAgendaResponse from(Agenda agenda) {
        this.voteResults = votes.stream().map(VoteResponseDto::from).toList();
        super.setFrom(agenda);
        return this;
    }
}
```

<br/><br/>

**_11. 시스템은 투표 결과가 조작되지 않음을 증명하기 위해 로그를 통한 감사를 지원해야 한다.   
이를 위해 특정 사용자가 투표한 결과를 실시간으로 기록해야 한다_**

```java
public void vote(User user, Agenda agenda, VoteType type, int quantity) {
    validate(user, agenda, quantity);
    agenda.vote(type, quantity);
    log.info("투표자={}, 안건={}, 표 종류={}, 표 개수={}", user.getName(), agenda.getTitle(), type.name(), quantity);
    voteRepository.save(Vote.create(user, agenda, type, quantity));
}
```
투표 시스템을 통해 투표를 수행할 경우, 표를 행사하는 사용자의 이름과 안건의 제목, 표 종류(찬성, 반대, 무효), 행사한 의결권 개수가    
타임스탬프와 함께 콘솔에 기록됨

<br/><br/>

**_12. 요구 사항 8번에 해당하는 의결권 선착순 제한 경쟁 방식은 여러 주주가 동시에 제한 경쟁에 참여하더라도 정상 동작함을 보장해야 한다.  
이를 테스트 코드를 통해 검증이 가능해야 한다_**

```java
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Agenda a where a.ID=:agendaId")
    Optional<Agenda> findByIdWithLock(@Param("agendaId") Long agendaId);
}
```
일반 투표와 선착순 투표 모두 동시에 요청이 전달될 수 있으므로,  

투표를 위해 서비스에서 해당 안건을 조회해올 때 다른 요청은 해당 안건의 수정이 끝날 때 까지 기다리도록   
비관적 쓰기락을 걸도록 구현하였음

```java
@DisplayName("동시에 선착순 투표를 수행해도 가장 빠른 10표만 반영된다.")
@Test
void concurrentLimitedVote() throws InterruptedException {
        // given
        AgendaCreateRequestDto agendaDto = new AgendaCreateRequestDto("사내 휴게시설 증진", AgendaType.LIMITED, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        Agenda agenda = agendaService.createAgenda(agendaDto);

        for (int i = 0; i < 5; i++) {
            UserJoinRequestDto userDto = new UserJoinRequestDto("test" + i, "1234", "name" + i, Role.USER, 10);
            userService.create(userDto);
        }

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(5);

        CountDownLatchT tt = new CountDownLatchT();
        for (int i = 1; i <= 5; i++) {
            executorService.execute(() -> {
                try {
                    tt.vote(agendaService, "test", agenda.getID(), VoteType.POSITIVE, 3);
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(InvalidVoteException.class);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // then
        countDownLatch.await();
        assertThat(agendaService.getAgenda(agenda.getID()).getTotalRights()).isEqualTo(10);
}

public static class CountDownLatchT {

        int count = 0;

        public void vote(AgendaService agendaService, String userId, Long agendaId, VoteType type, int quantity) {
            agendaService.vote(userId + count++, agendaId, type, quantity);
        }
}
```
선착순 투표의 동시성 테스트를 위해 자바에서 제공하는 `executorService`를 활용하여 병렬 수행 테스트를 구현하였고,   

동시의 요청에도 결과적으로 10개의 표만 반영이 되고 이후의 요청은 투표 종료로 인한 예외를 던지는 부분도 확인하였음

<br/><br/>

**_13. 요구 사항 9번에 해당하는 투표 시스템은 테스트 코드를 통해 검증할 수 있어야 한다_**

```java
@DisplayName("투표를 수행하면 투표 종류에 따라 안건에 표가 누적된다.")
@Test
void voteNormalAgenda() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5)));

        User user = userRepository.save(User.builder()
                .userId("test")
                .password("1234")
                .name("홍길동")
                .role(Role.USER)
                .voteRights(50)
                .build()
        );
        VoteType type = VoteType.POSITIVE;
        int quantity = 15;

        // when
        Agenda agendaResult = agendaService.vote(user.getUserId(), agenda.getID(), type, quantity);

        // then
        assertThat(agendaResult.getPositiveRights()).isEqualTo(15);
}
```
해당 테스트를 통해 일반 투표는 사용자가 가진 의결권 내에서 의결권을 행사할 수 있고,  
이는 안건의 투표 현황에 누적되는 것을 확인하였음

```java
@DisplayName("사용자가 가진 투표수 이하의 투표권만 행사할 수 있다.")
@Test
void overVote() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));

        User user = userRepository.save(User.builder()
                .userId("test")
                .password("1234")
                .name("홍길동")
                .role(Role.USER)
                .voteRights(10)
                .build()
        );
        VoteType type = VoteType.POSITIVE;
        int quantity = 11;

        // when&then
        assertThatThrownBy(() -> {
            agendaService.vote(user.getUserId(), agenda.getID(), type, quantity);
        }).isInstanceOf(InvalidVoteException.class);
}
```
또한 현재 가진 의결권 이상으로 행사할 경우, 의결권 초과 예외가 발생하는 과정도 확인하였음

<br/>

---

## 최적화

<br/><br/>

```properties
spring:
  jpa:
    open-in-view: false
```
OSIV 옵션을 끔으로써 DB connection을 효율적으로 관리할 수 있도록 하였음

<br/>

```properties
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(indexes = {
@Index(columnList = "agenda_id"),
@Index(columnList = "agenda_id, user_id")
})
public class Vote extends CreatedAtBaseEntity {
    ...
}
```
투표 테이블에 대해 복합 인덱스와 단일 인덱스를 통해 조회 성능을 향상시켰음   
`100000개의 투표 + 5개 안건에 대한 평균 조회 시간 (154ms -> 41ms) 확인`

<br/>

---

## 예외 처리

<br/><br/>

### 1. 상태코드와 메시지를 포함한 에러코드

예외가 발생했을 경우 전달할 코드와 메시지를 **Enum type** 으로 정의


우선 **에러 코드 인터페이스**를 정의하고,   
보편적인 예외에 따른 코드인지, 사용자가 정의한 예외에 따른 코드인지를 구별해서 구현하였음

```java
public interface ErrorCode {

    HttpStatus getHttpStatus();
    String getMessage();
}
```

```java
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    NO_ELEMENT(HttpStatus.NOT_FOUND, "Element not exists"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
```

```java
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    EMPTY_CLAIM(HttpStatus.UNAUTHORIZED, "No authentication information in claim"),
    DUPLICATED_ID(HttpStatus.CONFLICT, "ID already exists"),
    WRONG_PERIOD(HttpStatus.BAD_REQUEST, "Not voting period"),
    EXCEED_VOTE(HttpStatus.BAD_REQUEST, "Exceeding the number of possible votes"),
    DUPLICATED_VOTE(HttpStatus.BAD_REQUEST, "Duplicate voting is not allowed")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
```

공통적인 예외로는

- 스프링 유효성 **검증 단계**에서 (Spring validation) 발생시키는 예외
- 잘못된 ID 를 통한 **자원 조회**에 대해 Optional 에서 기본적으로 발생시키는 예외

두 가지 예외를 다루도록 설계 하였고,

사용자 정의 예외로는

- 토큰에 **클레임 정보가 없을 때** 예외
- **중복된 ID** 로 회원 가입을 요청할 때 예외
- **올바르지 못한 투표 요청**을 보냈을 때 예외

세 가지 예외를 다루도록 설계하였음

<br/>

### 2. 예외 정의

```java
@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;
}
```

```java
public class InvalidVoteException extends RestApiException {

    public InvalidVoteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

```java
public class DuplicateUserException extends RestApiException {

    public DuplicateUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

우선 예외 내 파라미터로 넣을 에러코드를 *RestApiException* 으로 정의하고,  
구체적인 예외를 **해당 예외 상속 받는 구조**로 정의하였음

위에서 정의한 에러코드를 파리미터로 전달하면, 예외 객체 내 에러 코드를 가지게 되고,  
해당 예외를 처리할 때 **메시지와 상태코드**를 확인 가능

<br/>

### 3. 글로벌 예외 처리 핸들러

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        ErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException e) {
        ErrorCode errorCode = CommonErrorCode.NO_ELEMENT;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }
}
```

최종적으로 서버에서 발생하는 예외를 받아서 처리하는 핸들러 클래스

상단에서 정의 했던 *RestApiException* 과 일반적으로 발생할 수 있는 예외에 대해 처리 과정을 작성하면,  
정의 했던 에러 코드를 통해 사용자에게 응답을 전달할 수 있게 됨

<br/>

---