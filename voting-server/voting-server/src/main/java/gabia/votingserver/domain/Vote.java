package gabia.votingserver.domain;

import gabia.votingserver.domain.auditing.CreatedAtBaseEntity;
import gabia.votingserver.domain.type.VoteType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(indexes = {
        @Index(columnList = "agenda_id"),
        @Index(columnList = "agenda_id, user_id")
})
public class Vote extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "agenda_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Agenda agenda;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VoteType type;

    @Column(nullable = false)
    private Integer rightCount;

    private Vote(User user, Agenda agenda, VoteType type, Integer rightCount) {
        this.user = user;
        this.agenda = agenda;
        this.type = type;
        this.rightCount = rightCount;
    }

    public static Vote create(User user, Agenda agenda, VoteType type, Integer rightCount) {
        return new Vote(user, agenda, type, rightCount);
    }
}
