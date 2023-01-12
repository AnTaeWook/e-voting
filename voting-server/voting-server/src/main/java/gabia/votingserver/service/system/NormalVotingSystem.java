package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.Vote;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

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
        if (now.isBefore(agenda.getStartsAt())) {
            throw new RuntimeException("투표 기간이 아닙니다.");
        }
        if (now.isAfter(agenda.getEndsAt())) {
            throw new RuntimeException("투표가 종료된 안건입니다.");
        }
    }

    private void validateUser(User user, int quantity) {
        if (user.getVoteRights() < quantity) {
            throw new RuntimeException("투표권 개수가 행사할 수 있는 개수를 초과하였습니다.");
        }
    }

    private void validateDuplicate(User user, Agenda agenda) {
        if (voteRepository.findVoteWithData(agenda, user).isPresent()) {
            throw new RuntimeException("이미 투표를 실시한 사용자 입니다.");
        }
    }
}
