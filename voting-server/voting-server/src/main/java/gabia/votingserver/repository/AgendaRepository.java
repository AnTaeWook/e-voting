package gabia.votingserver.repository;

import gabia.votingserver.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
