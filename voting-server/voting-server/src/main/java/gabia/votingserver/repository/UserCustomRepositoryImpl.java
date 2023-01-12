package gabia.votingserver.repository;

import gabia.votingserver.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public void directSave(User user) {
        entityManager.persist(user);
    }
}
