package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.AgendaType;
import lombok.Data;

@Data
public class AgendaCreateRequestDto {
    private String title;
    private AgendaType type;
    private MyDateTime startsAt;
    private MyDateTime endsAt;
}