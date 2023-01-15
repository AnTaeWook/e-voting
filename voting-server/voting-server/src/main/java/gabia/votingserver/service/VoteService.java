package gabia.votingserver.service;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.Vote;
import gabia.votingserver.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VoteService {

    private final VoteRepository voteRepository;

    public List<Vote> getVotesWithAgenda(Agenda agenda) {
        return voteRepository.findAllWithAgenda(agenda);
    }
}
