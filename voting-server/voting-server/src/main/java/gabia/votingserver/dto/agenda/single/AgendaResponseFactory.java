package gabia.votingserver.dto.agenda.single;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.Vote;
import gabia.votingserver.domain.type.Role;

import java.time.LocalDateTime;
import java.util.List;

public class AgendaResponseFactory {

    public static SimpleAgendaResponseDto getDto(Role role, Agenda agenda) {
        if (LocalDateTime.now().isAfter(agenda.getEndsAt())) {
            return new ResultAgendaResponseDto().from(agenda);
        }
        return switch (role) {
            case USER -> new SimpleAgendaResponseDto().from(agenda);
            case ADMIN -> new ResultAgendaResponseDto().from(agenda);
        };
    }

    public static SimpleAgendaResponseDto getDto(Agenda agenda, List<Vote> votes) {
        return new DetailResultAgendaResponse(votes).from(agenda);
    }
}
