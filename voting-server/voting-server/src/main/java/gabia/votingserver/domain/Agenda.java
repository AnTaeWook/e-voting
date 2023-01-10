package gabia.votingserver.domain;

import gabia.votingserver.dto.agenda.AgendaCreateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Agenda {

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

    @Setter
    @Column(nullable = false)
    private LocalDateTime endsAt;

    private Agenda(String title, AgendaType type, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.type = type;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public static Agenda of(AgendaCreateRequestDto requestDto) {
        return new Agenda(requestDto.getTitle(), requestDto.getType(),
                requestDto.getStartsAt().getLocalDateTime(), requestDto.getEndsAt().getLocalDateTime());
    }

    public static Agenda create(String title, AgendaType type, LocalDateTime startsAt, LocalDateTime endsAt) {
        return new Agenda(title, type, startsAt, endsAt);
    }
}
