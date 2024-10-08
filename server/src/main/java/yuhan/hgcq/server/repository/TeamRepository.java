package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Team;

@Repository
@RequiredArgsConstructor
public class TeamRepository {
    @PersistenceContext
    private final EntityManager em;

    public Long save(Team team) {
        if (team.getId() == null) {
            em.persist(team);
        } else {
            em.merge(team);
        }
        return team.getId();
    }

    public void delete(Long id) {
        Team find = findOne(id);
        em.remove(find);
    }

    public Team findOne(Long id) {
        return em.find(Team.class, id);
    }
}
