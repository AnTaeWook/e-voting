package gabia.votingserver.service.system;

import gabia.votingserver.domain.Agenda;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotingSystemFactory {

    private final NormalVotingSystem normalVotingSystem;
    private final LimitedVotingSystem limitedVotingSystem;

    public NormalVotingSystem makeVotingSystem(Agenda agenda) {
        return switch (agenda.getType()) {
            case NORMAL -> normalVotingSystem;
            case LIMITED -> limitedVotingSystem;
        };
    }
}
