package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.type.VoteType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VoteRequestDto {

    private VoteType type;

    @Positive
    private int quantity;
}
