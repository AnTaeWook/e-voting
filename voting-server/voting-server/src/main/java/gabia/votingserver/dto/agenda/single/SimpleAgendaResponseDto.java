package gabia.votingserver.dto.agenda.single;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.type.AgendaType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleAgendaResponseDto {

    private Long id;
    private String title;
    private AgendaType type;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public void setFrom(Agenda agenda) {
        this.setId(agenda.getID());
        this.setTitle(agenda.getTitle());
        this.setType(agenda.getType());
        this.setStartsAt(agenda.getStartsAt());
        this.setEndsAt(agenda.getEndsAt());
    }

    public SimpleAgendaResponseDto from(Agenda agenda) {
        setFrom(agenda);
        return this;
    }
}
