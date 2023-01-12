package gabia.votingserver.repository;

import gabia.votingserver.domain.Agenda;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Agenda a where a.ID=:agendaId")
    Agenda findByIdWithLock(@Param("agendaId") Long agendaId);
}
