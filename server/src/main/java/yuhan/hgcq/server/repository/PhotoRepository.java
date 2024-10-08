package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoRepository {
    @PersistenceContext
    private final EntityManager em;

    public Long save(Photo photo) {
        if (photo.getId() == null) {
            em.persist(photo);
        } else {
            em.merge(photo);
        }
        return photo.getId();
    }

    public void delete(Long id) {
        Photo find = findOne(id);
        em.remove(find);
    }

    public Photo findOne(Long id) {
        return em.find(Photo.class, id);
    }

    public Photo findByPath(String path) {
        try {
            return em.createQuery("select p from Photo p where p.path = :path", Photo.class)
                    .setParameter("path", path)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Photo> findAll(Album album) {
        return em.createQuery("select p from Photo p where p.album = :album and p.isDeleted = false order by p.created", Photo.class)
                .setParameter("album", album)
                .getResultList();
    }

    public List<String> findNameAll(Album album) {
        return em.createQuery("select p.name from Photo p where p.album = :album and p.isDeleted = false order by p.album.name", String.class)
                .setParameter("album", album)
                .getResultList();
    }

    public List<Photo> findByDeleted(Album album) {
        return em.createQuery("select p from Photo p where p.isDeleted = true order by p.created desc", Photo.class)
                .getResultList();
    }

    public void deleteAll(Album album) {
        em.createQuery("delete from Photo p where p.album = :album")
                .setParameter("album", album)
                .executeUpdate();
    }

    public void deleteByTeam(Team team) {
        em.createQuery("delete from Photo p where p.album.team = :team")
                .setParameter("team", team)
                .executeUpdate();
    }
}