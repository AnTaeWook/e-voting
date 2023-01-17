package gabia.votingserver.dto.agenda.single;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.type.AgendaType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAgendaResponseDto {

    private Long id;
    private String title;
    private AgendaType type;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public void setFrom(Agenda agenda) {
        this.id = agenda.getID();
        this.title = agenda.getTitle();
        this.type = agenda.getType();
        this.startsAt = agenda.getStartsAt();
        this.endsAt = agenda.getEndsAt();
    }

    public SimpleAgendaResponseDto from(Agenda agenda) {
        return new SimpleAgendaResponseDto(
                agenda.getID(),
                agenda.getTitle(),
                agenda.getType(),
                agenda.getStartsAt(),
                agenda.getEndsAt()
        );
    }
}
