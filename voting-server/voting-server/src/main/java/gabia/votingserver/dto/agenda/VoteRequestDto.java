package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.type.VoteType;
import lombok.Data;

@Data
public class VoteRequestDto {

    private VoteType type;
    private int quantity;
}
