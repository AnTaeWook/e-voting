package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.repository.VoteRepository;
import org.springframework.stereotype.Component;

@Component
public class LimitedVotingSystem extends NormalVotingSystem {

    private static final int MAX_VOTE_COUNT = 10;

    public LimitedVotingSystem(VoteRepository voteRepository) {
        super(voteRepository);
    }

    @Override
    public void vote(User user, Agenda agenda, VoteType type, int quantity) {
        quantity = Math.min(quantity, MAX_VOTE_COUNT - agenda.getTotalRights());
        super.vote(user, agenda, type, quantity);
        if (agenda.getTotalRights() >= MAX_VOTE_COUNT) {
            agenda.terminate();
        }
    }
}
