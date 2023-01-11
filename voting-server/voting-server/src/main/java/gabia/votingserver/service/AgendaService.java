package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import gabia.votingserver.repository.AgendaRepository;
import gabia.votingserver.repository.VoteRepository;
import gabia.votingserver.service.system.NormalVotingSystem;
import gabia.votingserver.service.system.VotingSystemFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AgendaService {
    private final AgendaRepository agendaRepository;
    private final UserService userService;
    private final VoteRepository voteRepository;

    public Page<Agenda> getAgendas(Pageable pageable) {
        return agendaRepository.findAll(pageable);
    }

    public Agenda getAgenda(long agendaId) {
        return getAgendaWithId(agendaId);
    }

    @Transactional
    public Agenda createAgenda(AgendaCreateRequestDto agendaCreateRequestDto) {
        return agendaRepository.save(Agenda.of(agendaCreateRequestDto));
    }

    @Transactional
    public void removeAgenda(long agendaId) {
        Agenda agenda = getAgendaWithId(agendaId);
        agendaRepository.delete(agenda);
    }

    @Transactional
    public Agenda terminate(long agendaId) {
        Agenda agenda = getAgendaWithId(agendaId);
        agenda.setEndsAt(LocalDateTime.now());
        return agenda;
    }

    @Transactional
    public Agenda vote(String userId, Long agendaID, VoteType type, int quantity) {
        User user = userService.getUser(userId);
        Agenda agenda = getAgendaWithId(agendaID);

        VotingSystemFactory factory = new VotingSystemFactory(voteRepository);
        NormalVotingSystem votingSystem = factory.makeVotingSystem(agenda);
        votingSystem.vote(user, agenda, type, quantity);
        return agenda;
    }

    private Agenda getAgendaWithId(Long agendaId) {
        Optional<Agenda> findAgenda = agendaRepository.findById(agendaId);
        validateAgenda(findAgenda);
        return findAgenda.get();
    }

    private void validateAgenda(Optional<Agenda> agenda) {
        if (agenda.isEmpty()) {
            throw new EntityNotFoundException("해당 Id에 해당하는 안건이 존재하지 않습니다.");
        }
    }
}
