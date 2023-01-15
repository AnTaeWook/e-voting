package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.type.VoteType;

public interface VotingSystem {

    void vote(User user, Agenda agenda, VoteType type, int quantity);
}
