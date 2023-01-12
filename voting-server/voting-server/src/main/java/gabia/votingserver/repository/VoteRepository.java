package gabia.votingserver.repository;

import gabia.votingserver.domain.Agenda;
import gabia.votingserver.domain.User;
import gabia.votingserver.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v where v.agenda=:agenda and v.user=:user")
    public Optional<Vote> findVoteWithData(@Param("agenda") Agenda agenda, @Param("user") User user);

    @Query("select v from Vote v join fetch v.user join fetch v.agenda where v.agenda=:agenda")
    public List<Vote> findAllWithAgenda(@Param("agenda") Agenda agenda);
}
