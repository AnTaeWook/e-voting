package gabia.votingserver.dto.vote;

import gabia.votingserver.domain.Vote;
import gabia.votingserver.domain.type.VoteType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteResponseDto {

    private String userName;
    private String agendaTitle;
    private VoteType type;
    private int rightCount;

    public static VoteResponseDto from(Vote vote) {
        return new VoteResponseDto(
                vote.getUser().getName(),
                vote.getAgenda().getTitle(),
                vote.getType(),
                vote.getRightCount()
        );
    }
}
