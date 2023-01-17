package gabia.votingserver.domain;

import gabia.votingserver.domain.auditing.CreatedAtAndModifiedAtBaseEntity;
import gabia.votingserver.domain.type.AgendaType;
import gabia.votingserver.domain.type.VoteType;
import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Agenda extends CreatedAtAndModifiedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AgendaType type;

    private int positiveRights = 0;

    private int negativeRights = 0;

    private int invalidRights = 0;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    private Agenda(String title, AgendaType type, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.type = type;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public void terminate() {
        this.endsAt = LocalDateTime.now();
    }

    public static Agenda of(AgendaCreateRequestDto requestDto) {
        return new Agenda(requestDto.getTitle(), requestDto.getType(),
                requestDto.getStartsAt(), requestDto.getEndsAt());
    }

    public static Agenda create(String title, AgendaType type, LocalDateTime startsAt, LocalDateTime endsAt) {
        return new Agenda(title, type, startsAt, endsAt);
    }

    public int getTotalRights() {
        return positiveRights + negativeRights + invalidRights;
    }

    public void vote(VoteType type, int quantity) {
        switch (type) {
            case POSITIVE -> positiveRights += quantity;
            case NEGATIVE -> negativeRights += quantity;
            case INVALID -> invalidRights += quantity;
        }
    }
}
