package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class AlbumRepository {
    @PersistenceContext
    private final EntityManager em;

    public Long save(Album album) {
        if (album.getId() == null) {
            em.persist(album);
        } else {
            em.merge(album);
        }
        return album.getId();
    }

    public void delete(Long id) {
        Album find = findOne(id);
        em.remove(find);
    }

    public void deleteByTeam(Team team) {
        em.createQuery("delete from Album a where a.team = :team")
                .setParameter("team", team)
                .executeUpdate();
    }

    public Album findOne(Long id) {
        return em.find(Album.class, id);
    }

    public Album findOneByName(Team team, String name) {
        return em.createQuery("select a from Album a where a.team = :team and a.name = :name", Album.class)
                .setParameter("team", team)
                .setParameter("name", name)
                .getResultList().get(0);
    }

    public List<Album> findAll(Team team) {
        return em.createQuery("select a from Album a where a.team = :team and a.isDeleted = false order by a.name", Album.class)
                .setParameter("team", team)
                .getResultList();
    }

    public List<Album> findByName(Team team, String name) {
        return em.createQuery("select a from Album a where a.team = :team and a.isDeleted = false and a.name like :name order by a.name", Album.class)
                .setParameter("team", team)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Album> findByDeleted(Team team) {
        return em.createQuery("select a from Album a where a.team = :team and a.isDeleted = true order by a.deletedAt", Album.class)
                .setParameter("team", team)
                .getResultList();
    }

    public Set<String> findAlbumName(Team team) {
        List<String> nameList = em.createQuery("select a.name from Album a where a.team = :team", String.class)
                .setParameter("team", team)
                .getResultList();
        return new HashSet<>(nameList);
    }
}
