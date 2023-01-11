package gabia.votingserver.dto.agenda.single;

import com.fasterxml.jackson.annotation.JsonProperty;
import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.Vote;
import gabia.votingserver.dto.vote.VoteResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DetailResultAgendaResponse extends ResultAgendaResponseDto {

    private final List<Vote> votes;

    @JsonProperty
    private List<VoteResponseDto> voteResults;

    @Override
    public DetailResultAgendaResponse from(Agenda agenda) {
        this.voteResults = votes.stream().map(VoteResponseDto::from).toList();
        super.setFrom(agenda);
        return this;
    }
}
