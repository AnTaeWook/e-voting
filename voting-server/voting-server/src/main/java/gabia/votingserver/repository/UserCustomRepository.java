package gabia.votingserver.repository;

import gabia.votingserver.domain.User;

public interface UserCustomRepository {

    void directSave(User user);
}
