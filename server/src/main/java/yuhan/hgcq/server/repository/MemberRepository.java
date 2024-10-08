package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @PersistenceContext
    private final EntityManager em;

    public Long save(Member member) {
        if (member.getId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
        return member.getId();
    }

    public void delete(Long memberId) {
        Member findMember = findOne(memberId);
        em.remove(findMember);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public Member findOne(String email) {
        try {
            return em.createQuery("select m from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name like :name", Member.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<String> findAllEmails() {
        return em.createQuery("select m.email from Member m order by m.email", String.class)
                .getResultList();
    }

    public List<String> findAllNames() {
        return em.createQuery("select m.name from Member m order by m.name", String.class)
                .getResultList();
    }
}
