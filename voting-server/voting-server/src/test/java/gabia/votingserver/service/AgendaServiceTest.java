package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.AgendaType;
import gabia.votingserver.domain.type.Role;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.repository.AgendaRepository;
import gabia.votingserver.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class AgendaServiceTest {

    @Autowired
    private AgendaService agendaService;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("안건을 조회하면 투표 시작 시간 순으로 오름차순 정렬하여 리턴됨")
    @Test
    void findAllAgenda() {
        // given
        long originAgendaCount = agendaRepository.count();

        agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));
        agendaRepository.save(Agenda.create("자율 출퇴근제 가능 시간 추가", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));
        agendaRepository.save(Agenda.create("글로벌 클라우드 마케팅", AgendaType.NORMAL,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5)));

        // when
        PageRequest request = PageRequest.of(0, 10, Sort.by("startsAt"));
        Page<Agenda> agendas = agendaService.getAgendas(request);

        // then
        assertThat(agendas.getTotalElements()).isEqualTo(originAgendaCount + 3);
        assertThat(agendas.get().toList().get(0).getTitle()).isEqualTo("글로벌 클라우드 마케팅");
    }

    @DisplayName("안건의 ID를 통해 단일 안건을 조회할 수 있다.")
    @Test
    void findAgenda() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));

        // when
        Agenda findAgenda = agendaService.getAgenda(agenda.getID());

        // then
        assertThat(agenda.getTitle()).isEqualTo("사내 휴식시설 증진");
    }

    @DisplayName("ID를 통해 안건을 삭제할 수 있다.")
    @Test
    void deleteAgenda() {
        // given
        Agenda agenda1 = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));
        agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));

        long originAgendaCount = agendaRepository.count();

        // when
        agendaService.removeAgenda(agenda1.getID());

        // then
        assertThat(agendaRepository.count()).isEqualTo(originAgendaCount - 1);
    }

    @DisplayName("관리자가 안건을 종료하면 안건 종료 시간이 관리자가 종료한 시각으로 바뀐다.")
    @Test
    void terminateAgenda() {
        // given
        Agenda agendaBefore = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));
        entityManager.detach(agendaBefore);

        // when
        Agenda agendaAfter = agendaService.terminate(agendaBefore.getID());

        // then
        assertThat(agendaAfter.getEndsAt().isBefore(agendaBefore.getEndsAt())).isTrue();
    }

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

    @DisplayName("중복 투표는 불허한다.")
    @Test
    void duplicateVote() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.now(),
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
        int quantity = 5;

        // when&then
        assertThatThrownBy(() -> {
            agendaService.vote(user.getUserId(), agenda.getID(), type, quantity);
            agendaService.vote(user.getUserId(), agenda.getID(), type, quantity);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("선착순 투표에 10표 이상 누적되면 투표가 종료된다.")
    @Test
    void voteLimitedAgenda() {
        // given
        LocalDateTime originEndsAt = LocalDateTime.now().plusDays(5);

        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.LIMITED,
                LocalDateTime.now(),
                originEndsAt));

        User user = userRepository.save(User.builder()
                .userId("test")
                .password("1234")
                .name("홍길동")
                .role(Role.USER)
                .voteRights(50)
                .build()
        );
        VoteType type = VoteType.POSITIVE;
        int quantity = 10;

        // when
        Agenda agendaResult = agendaService.vote(user.getUserId(), agenda.getID(), type, quantity);

        // then
        assertThat(agendaResult.getEndsAt().isBefore(originEndsAt)).isTrue();
    }

    @DisplayName("선착순 투표에 10표보다 많은 표를 받으면 가능한 표 개수 만큼만 적용된다")
    @Test
    void voteLimitedAgendaOverQuantity() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.LIMITED,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)));

        User user = userRepository.save(User.builder()
                .userId("test")
                .password("1234")
                .name("홍길동")
                .role(Role.USER)
                .voteRights(50)
                .build()
        );
        User user2 = userRepository.save(User.builder()
                .userId("test2")
                .password("1234")
                .name("임꺽정")
                .role(Role.USER)
                .voteRights(50)
                .build()
        );
        VoteType type = VoteType.POSITIVE;

        // when
        agendaService.vote(user.getUserId(), agenda.getID(), type, 5);
        Agenda agendaResult = agendaService.vote(user2.getUserId(), agenda.getID(), type, 7);

        // then
        assertThat(agenda.getTotalRights()).isEqualTo(10);
    }
}