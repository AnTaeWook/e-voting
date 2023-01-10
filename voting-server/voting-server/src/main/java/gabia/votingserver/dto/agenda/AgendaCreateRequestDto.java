package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.AgendaType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AgendaCreateRequestDto {
    private String title;
    private AgendaType type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startsAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endsAt;
}