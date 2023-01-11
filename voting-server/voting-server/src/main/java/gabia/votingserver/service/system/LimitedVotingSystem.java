package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.repository.VoteRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class LimitedVotingSystem extends NormalVotingSystem {

    public LimitedVotingSystem(VoteRepository voteRepository) {
        super(voteRepository);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void vote(User user, Agenda agenda, VoteType type, int quantity) {
        quantity = Math.min(quantity, 10 - agenda.getTotalRights());
        super.vote(user, agenda, type, quantity);
        if (agenda.getTotalRights() >= 10) {
            agenda.setEndsAt(LocalDateTime.now());
        }
    }

    @Override
    protected void validate(User user, Agenda agenda, int quantity) {
        super.validate(user, agenda, quantity);
        if (agenda.getTotalRights() >= 10) {
            throw new RuntimeException("선착순 투표가 마감되었습니다.");
        }
    }
}
