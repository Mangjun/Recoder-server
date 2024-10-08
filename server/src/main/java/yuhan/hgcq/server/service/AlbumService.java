package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.repository.AlbumRepository;
import yuhan.hgcq.server.repository.TeamMemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {
    private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

    private final AlbumRepository ar;
    private final TeamMemberRepository tmr;

    private final static int DELETE_DAY = 30;

    /**
     * Create album
     *
     * @param member member
     * @param album  album
     * @return albumId
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public Long create(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");
        ensureNotNull(member, "Member");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            ar.save(album);
            log.info("Create Album : {}", album);
            return album.getId();
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Update album information
     *
     * @param member member
     * @param album  album
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void modify(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            ar.save(album);
            log.info("Modify Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Delete album
     *
     * @param member member
     * @param album  album
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void deleteAlbum(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            album.deleteAlbum();
            ar.save(album);
            log.info("Delete Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Delete album cancel
     *
     * @param member member
     * @param album  album
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void deleteAlbumCancel(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            album.cancelDeleteAlbum();
            ar.save(album);
            log.info("Cancel Delete Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Trash empty
     *
     * @param albums albumList
     */
    @Transactional
    public void trash(List<Album> albums) {
        LocalDateTime now = LocalDateTime.now();
        for (Album album : albums) {
            LocalDate deletedAt = album.getDeletedAt();
            long between = ChronoUnit.DAYS.between(deletedAt, now);

            if (between >= DELETE_DAY) {
                ar.delete(album.getId());
                log.info("Complete Delete Album : {}", album);
            }
        }

    }

    /**
     * Find album
     *
     * @param id albumId
     * @return album
     * @throws IllegalArgumentException Argument is wrong
     */
    public Album searchOne(Long id) throws IllegalArgumentException {
        Album find = ar.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("Album not found");
        }

        return find;
    }

    /**
     * Find albumList
     *
     * @param team team
     * @return albumList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Album> searchAll(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return ar.findAll(team);
    }

    /**
     * Find album by name
     *
     * @param team team
     * @param name album name
     * @return album
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Album> searchByName(Team team, String name) throws IllegalArgumentException {
        ensureNotNull(team, "Team");
        ensureNotNull(name, "Name");

        return ar.findByName(team, name);
    }

    /**
     * Find albumTrashList
     *
     * @param team team
     * @return albumTrashList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Album> searchAlbumTrashList(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return ar.findByDeleted(team);
    }

    /**
     * Argument Check if Null
     *
     * @param obj  argument
     * @param name by log
     */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /**
     * Check member is admin
     *
     * @param member member
     * @param album  album
     * @return is admin?
     */
    private boolean isAdmin(Member member, Album album) {
        Team team = album.getTeam();

        List<Member> adminList = tmr.findAdminByTeam(team);

        return adminList.contains(member);
    }
}
