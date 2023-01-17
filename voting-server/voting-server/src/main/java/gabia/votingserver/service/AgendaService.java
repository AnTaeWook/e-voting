package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import gabia.votingserver.repository.AgendaRepository;
import gabia.votingserver.service.system.VotingSystem;
import gabia.votingserver.service.system.VotingSystemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UserService userService;
    private final VotingSystemFactory votingSystemFactory;

    public Page<Agenda> getAgendas(Pageable pageable) {
        return agendaRepository.findAll(pageable);
    }

    public Agenda getAgenda(long agendaId) {
        return agendaRepository.findById(agendaId).orElseThrow();
    }

    @Transactional
    public Agenda createAgenda(AgendaCreateRequestDto agendaCreateRequestDto) {
        return agendaRepository.save(Agenda.of(agendaCreateRequestDto));
    }

    @Transactional
    public void removeAgenda(long agendaId) {
        agendaRepository.delete(agendaRepository.findById(agendaId).orElseThrow());
    }

    @Transactional
    public Agenda terminateAgenda(long agendaId) {
        Agenda agenda = agendaRepository.findById(agendaId).orElseThrow();
        agenda.terminate();
        return agenda;
    }

    @Transactional
    public Agenda vote(String userId, Long agendaID, VoteType type, int quantity) {
        User user = userService.getUser(userId);
        Agenda agenda = agendaRepository.findByIdWithLock(agendaID).orElseThrow();

        VotingSystem votingSystem = votingSystemFactory.makeVotingSystem(agenda);
        votingSystem.vote(user, agenda, type, quantity);
        return agenda;
    }
}
