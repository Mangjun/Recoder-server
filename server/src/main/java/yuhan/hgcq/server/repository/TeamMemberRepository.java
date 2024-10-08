package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    @PersistenceContext
    private final EntityManager em;

    public void save(TeamMember teamMember) {
        em.persist(teamMember);
    }

    public void delete(TeamMember teamMember) {
        em.remove(teamMember);
    }

    public TeamMember findOne(Member member, Team team) {
        try {
            return em.createQuery("select tm from TeamMember tm where tm.member = :member and tm.team = :team", TeamMember.class)
                    .setParameter("member", member)
                    .setParameter("team", team)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void update(TeamMember teamMember) {
        em.merge(teamMember);
    }

    public void deleteAll(Team team) {
        em.createQuery("delete from TeamMember tm where tm.team = :team")
                .setParameter("team", team)
                .executeUpdate();
    }

    public List<Team> findAll(Member member) {
        return em.createQuery("select tm.team from TeamMember tm where tm.member = :member order by tm.team.name asc", Team.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Team> findByName(Member member, String name) {
        return em.createQuery("select tm.team from TeamMember tm where tm.member = :member and tm.team.name like :name order by tm.team.name asc", Team.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Member> findByTeam(Team team) {
        return em.createQuery("select tm.member from TeamMember tm where tm.team = :team order by tm.isAdmin desc, tm.member.name asc", Member.class)
                .setParameter("team", team)
                .getResultList();
    }

    public List<Member> findAdminByTeam(Team team) {
        return em.createQuery("select tm.member from TeamMember tm where tm.team = :team and tm.isAdmin = true", Member.class)
                .setParameter("team", team)
                .getResultList();
    }
}
