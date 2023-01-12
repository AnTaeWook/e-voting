package gabia.votingserver.dto.agenda.single;

import com.fasterxml.jackson.annotation.JsonProperty;
import gabia.votingserver.domain.Agenda;

public class ResultAgendaResponseDto extends SimpleAgendaResponseDto {

    @JsonProperty
    private int positiveRights;

    @JsonProperty
    private int negativeRights;

    @JsonProperty
    private int invalidRights;

    public void setFrom(Agenda agenda) {
        super.setFrom(agenda);
        this.positiveRights = agenda.getPositiveRights();
        this.negativeRights = agenda.getNegativeRights();
        this.invalidRights = agenda.getInvalidRights();
    }

    @Override
    public ResultAgendaResponseDto from(Agenda agenda) {
        setFrom(agenda);
        return this;
    }
}

