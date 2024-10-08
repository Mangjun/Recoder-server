package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Team;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    @PersistenceContext
    private final EntityManager em;

    public Long save(Chat chat) {
        if (chat.getId() == null) {
            em.persist(chat);
        } else {
            em.merge(chat);
        }
        return chat.getId();
    }

    public void delete(Long id) {
        Chat find = findOne(id);
        em.remove(find);
    }

    public void deleteByTeam(Team team) {
        em.createQuery("delete from Chat c where c.album.team = :team")
                .setParameter("team", team)
                .executeUpdate();
    }

    public Chat findOne(Long id) {
        return em.find(Chat.class, id);
    }

    public List<Chat> findAll(Album album) {
        return em.createQuery("select c from Chat c where c.album = :album order by c.time", Chat.class)
                .setParameter("album", album)
                .getResultList();
    }
}
