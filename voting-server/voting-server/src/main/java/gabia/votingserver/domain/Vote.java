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
public class Vote extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    @ManyToOne
    private Agenda agenda;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VoteType type;

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
