package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.type.AgendaType;
import gabia.votingserver.repository.AgendaRepository;
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

@Transactional
@SpringBootTest
class AgendaServiceTest {

    @Autowired
    private AgendaService agendaService;
    @Autowired
    private AgendaRepository agendaRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("안건을 조회하면 투표 시작 시간 순으로 오름차순 정렬하여 리턴됨")
    @Test
    void findAllAgenda() {
        // given
        agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 15, 15, 0),
                LocalDateTime.of(2023, 2, 10, 15, 0)));
        agendaRepository.save(Agenda.create("자율 출퇴근제 가능 시간 추가", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 14, 15, 0),
                LocalDateTime.of(2023, 2, 11, 15, 0)));
        agendaRepository.save(Agenda.create("글로벌 클라우드 마케팅", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 13, 15, 0),
                LocalDateTime.of(2023, 2, 12, 15, 0)));

        // when
        PageRequest request = PageRequest.of(0, 10, Sort.by("startsAt"));
        Page<Agenda> agendas = agendaService.getAgendas(request);

        // then
        assertThat(agendas.getTotalElements()).isEqualTo(3);
        assertThat(agendas.get().toList().get(0).getTitle()).isEqualTo("글로벌 클라우드 마케팅");
    }

    @DisplayName("안건의 ID를 통해 단일 안건을 조회할 수 있다.")
    @Test
    void findAgenda() {
        // given
        Agenda agenda = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 15, 15, 0),
                LocalDateTime.of(2023, 2, 10, 15, 0)));

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
                LocalDateTime.of(2023, 1, 15, 15, 0),
                LocalDateTime.of(2023, 2, 10, 15, 0)));
        Agenda agenda2 = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 15, 15, 0),
                LocalDateTime.of(2023, 2, 10, 15, 0)));

        // when
        agendaService.removeAgenda(agenda1.getID());

        // then
        assertThat(agendaRepository.findAll().size()).isEqualTo(1);
    }

    @DisplayName("관리자가 안건을 종료하면 안건 종료 시간이 관리자가 종료한 시각으로 바뀐다.")
    @Test
    void terminateAgenda() {
        // given
        Agenda agendaBefore = agendaRepository.save(Agenda.create("사내 휴식시설 증진", AgendaType.NORMAL,
                LocalDateTime.of(2023, 1, 15, 15, 0),
                LocalDateTime.of(2023, 2, 10, 15, 0)));
        entityManager.detach(agendaBefore);

        // when
        Agenda agendaAfter = agendaService.terminate(agendaBefore.getID());

        // then
        assertThat(agendaAfter.getEndsAt().isBefore(agendaBefore.getEndsAt())).isTrue();
    }
}