package gabia.votingserver.domain.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class CreatedAtAndModifiedAtBaseEntity extends CreatedAtBaseEntity {

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;
}
