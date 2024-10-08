package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Liked;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.repository.LikedRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikedService {
    private static final Logger log = LoggerFactory.getLogger(LikedService.class);

    private final LikedRepository lr;

    /**
     * Add like
     *
     * @param liked like
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void addLike(Liked liked) throws IllegalArgumentException {
        ensureNotNull(liked, "Liked");

        Member member = liked.getMember();
        Photo photo = liked.getPhoto();

        Liked find = lr.findOne(member, photo);

        if (find == null) {
            lr.save(liked);
        } else {
            liked.addLiked();
            lr.update(liked);
        }
        log.info("Add Like : {}", liked);
    }

    /**
     * Remove like
     *
     * @param liked like
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void removeLike(Liked liked) throws IllegalArgumentException {
        ensureNotNull(liked, "Liked");

        liked.cancelLiked();

        lr.update(liked);
        log.info("Remove Like : {}", liked);
    }

    /**
     * Find like
     *
     * @param member member
     * @param photo  photo
     * @return like
     * @throws IllegalArgumentException Argument is wrong
     */
    public Liked searchOne(Member member, Photo photo) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(photo, "Photo");

        Liked find = lr.findOne(member, photo);

        if (find == null) {
            throw new IllegalStateException("Liked is null");
        }

        return find;
    }

    /**
     * Find likeList
     *
     * @param member member
     * @return likeList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Photo> searchAll(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return lr.findAll(member);
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
}
