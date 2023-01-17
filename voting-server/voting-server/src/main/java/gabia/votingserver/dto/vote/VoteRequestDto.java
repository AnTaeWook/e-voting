package gabia.votingserver.dto.vote;

import gabia.votingserver.domain.type.VoteType;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteRequestDto {

    @Positive
    private Long agendaId;

    private VoteType type;

    @Positive
    private int quantity;
}
