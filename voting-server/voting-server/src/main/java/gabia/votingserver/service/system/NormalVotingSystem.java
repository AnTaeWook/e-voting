package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.Vote;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.error.code.UserErrorCode;
import gabia.votingserver.error.exception.InvalidVoteException;
import gabia.votingserver.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class NormalVotingSystem implements VotingSystem {

    private final VoteRepository voteRepository;

    public void vote(User user, Agenda agenda, VoteType type, int quantity) {
        validate(user, agenda, quantity);
        agenda.vote(type, quantity);
        log.info("투표자={}, 안건={}, 표 종류={}, 표 개수={}", user.getName(), agenda.getTitle(), type.name(), quantity);
        voteRepository.save(Vote.create(user, agenda, type, quantity));
    }

    protected void validate(User user, Agenda agenda, int quantity) {
        validateAgenda(agenda);
        validateUser(user, quantity);
        validateDuplicate(user, agenda);
    }

    private void validateAgenda(Agenda agenda) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(agenda.getStartsAt()) || now.isAfter(agenda.getEndsAt())) {
            throw new InvalidVoteException(UserErrorCode.WRONG_PERIOD);
        }
    }

    private void validateUser(User user, int quantity) {
        if (user.getVoteRights() < quantity) {
            throw new InvalidVoteException(UserErrorCode.EXCEED_VOTE);
        }
    }

    private void validateDuplicate(User user, Agenda agenda) {
        if (voteRepository.findVoteWithData(agenda, user).isPresent()) {
            throw new InvalidVoteException(UserErrorCode.DUPLICATED_VOTE);
        }
    }
}
