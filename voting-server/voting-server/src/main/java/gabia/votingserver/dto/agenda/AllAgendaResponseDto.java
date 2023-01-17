package gabia.votingserver.dto.agenda;

import gabia.votingserver.domain.Agenda;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllAgendaResponseDto {

    private Long id;
    private String title;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public static AllAgendaResponseDto from(Agenda agenda) {
        return new AllAgendaResponseDto(
                agenda.getID(),
                agenda.getTitle(),
                agenda.getStartsAt(),
                agenda.getEndsAt()
        );
    }
}
