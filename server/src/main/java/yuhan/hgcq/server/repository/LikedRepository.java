package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikedRepository {
    @PersistenceContext
    private final EntityManager em;

    public void save(Liked liked) {
        em.persist(liked);
    }

    public Liked findOne(Member member, Photo photo) {
        try {
            return em.createQuery("select l from Liked l where l.member = :member and l.photo = :photo", Liked.class)
                    .setParameter("member", member)
                    .setParameter("photo", photo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void delete(Photo photo) {
        em.createQuery("delete from Liked l where l.photo = :photo")
                .setParameter("photo", photo)
                .executeUpdate();
    }

    public void deleteByTeam(Team team) {
        em.createQuery("delete from Liked l where l.photo.album.team = :team")
                .setParameter("team", team)
                .executeUpdate();
    }

    public void deleteByAlbum(Album album) {
        em.createQuery("delete from Liked l where l.photo.album = :album")
                .setParameter("album", album)
                .executeUpdate();
    }

    public void update(Liked liked) {
        em.merge(liked);
    }

    public List<Photo> findAll(Member member) {
        return em.createQuery("select l.photo from Liked l where l.member = :member and l.isLiked = true order by l.photo.created", Photo.class)
                .setParameter("member", member)
                .getResultList();
    }
}
