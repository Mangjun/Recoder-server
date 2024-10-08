package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepository {
    @PersistenceContext
    private final EntityManager em;

    public void save(Follow follow) {
        em.persist(follow);
    }

    public void delete(Follow follow) {
        em.remove(follow);
    }

    public Follow findOne(Member member, Member follow) {
        try {
            return em.createQuery("select f from Follow f where f.member = :member and f.follow = :follow", Follow.class)
                    .setParameter("member", member)
                    .setParameter("follow", follow)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Member> findFollowingListByName(Member member, String name) {
        return em.createQuery("select f.follow from Follow f where f.member = :member and f.follow.name LIKE :name order by f.follow.name", Member.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Member> findFollowingList(Member member) {
        return em.createQuery("select f.follow from Follow f where f.member = :member order by f.follow.name", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Member> findFollowerList(Member member) {
        return em.createQuery("select f.member from Follow f where f.follow = :member order by f.member.name", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<Member> findFollowerListByName(Member member, String name) {
        return em.createQuery("select f.member from Follow f where f.follow = :member and f.member.name like :name order by f.member.name", Member.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
}
