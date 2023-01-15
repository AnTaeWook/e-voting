package gabia.votingserver.dto.vote;

import gabia.votingserver.domain.type.VoteType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VoteRequestDto {

    @Positive
    private Long agendaId;

    private VoteType type;

    @Positive
    private int quantity;
}
