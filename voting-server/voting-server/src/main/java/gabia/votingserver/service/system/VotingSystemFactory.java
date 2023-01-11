package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.repository.VoteRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VotingSystemFactory {

    private final VoteRepository voteRepository;

    public NormalVotingSystem makeVotingSystem(Agenda agenda) {
        return switch (agenda.getType()) {
            case NORMAL -> new NormalVotingSystem(voteRepository);
            case LIMITED -> new LimitedVotingSystem(voteRepository);
        };
    }
}
