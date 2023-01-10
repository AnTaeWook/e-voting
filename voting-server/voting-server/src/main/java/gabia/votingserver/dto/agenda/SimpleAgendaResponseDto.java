package gabia.votingserver.dto.agenda;

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

    public static SimpleAgendaResponseDto from(Agenda agenda) {
        SimpleAgendaResponseDto simpleAgendaResponseDto = new SimpleAgendaResponseDto();
        simpleAgendaResponseDto.setId(agenda.getID());
        simpleAgendaResponseDto.setTitle(agenda.getTitle());
        simpleAgendaResponseDto.setType(agenda.getType());
        simpleAgendaResponseDto.setStartsAt(agenda.getStartsAt());
        simpleAgendaResponseDto.setEndsAt(agenda.getEndsAt());
        return simpleAgendaResponseDto;
    }
}
