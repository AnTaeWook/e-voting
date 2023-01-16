package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.type.AgendaType;
import gabia.votingserver.domain.type.Role;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import gabia.votingserver.dto.user.UserJoinRequestDto;
import gabia.votingserver.error.exception.InvalidVoteException;
import gabia.votingserver.repository.AgendaRepository;
import gabia.votingserver.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AgendaConcurrentServiceTest {

    @Autowired
    private AgendaService agendaService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private UserRepository userRepository;
    @AfterEach
    void clear() {
        agendaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("동시에 투표를 수행해도 순차적으로 표가 누적된다.")
    @Test
    void concurrentVote() throws InterruptedException {
        // given
        AgendaCreateRequestDto agendaDto = new AgendaCreateRequestDto("사내 휴게시설 증진", AgendaType.NORMAL, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
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
                tt.vote(agendaService, "test", agenda.getID(), VoteType.POSITIVE, 3);
                countDownLatch.countDown();
            });
        }

        // then
        countDownLatch.await();
        assertThat(agendaService.getAgenda(agenda.getID()).getTotalRights()).isEqualTo(15);
    }

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
}
