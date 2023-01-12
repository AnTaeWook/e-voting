package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.repository.VoteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LimitedVotingSystem extends NormalVotingSystem {

    public LimitedVotingSystem(VoteRepository voteRepository) {
        super(voteRepository);
    }

    @Override
    public void vote(User user, Agenda agenda, VoteType type, int quantity) {
        quantity = Math.min(quantity, 10 - agenda.getTotalRights());
        super.vote(user, agenda, type, quantity);
        if (agenda.getTotalRights() >= 10) {
            agenda.setEndsAt(LocalDateTime.now());
        }
    }
}
