package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.Agenda;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class AllAgendaResponseDto {

    private Long id;
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public AllAgendaResponseDto(Long id, String title, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.id = id;
        this.title = title;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public static AllAgendaResponseDto from(Agenda agenda) {
        return new AllAgendaResponseDto(
                agenda.getID(),
                agenda.getTitle(),
                agenda.getStartsAt(),
                agenda.getEndsAt()
        );
    }
}
