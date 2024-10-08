package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.repository.FollowRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {
    private static final Logger log = LoggerFactory.getLogger(FollowService.class);

    private final FollowRepository fr;

    /**
     * Add follow
     *
     * @param follow follow
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void addFollow(Follow follow) throws IllegalArgumentException {
        ensureNotNull(follow, "Follow");

        fr.save(follow);
        log.info("Create Follow : {}", follow);
    }

    /**
     * Remove follow
     *
     * @param follow follow
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void removeFollow(Follow follow) throws IllegalArgumentException {
        ensureNotNull(follow, "Follow");

        fr.delete(follow);
        log.info("Delete Follow : {}", follow);
    }

    /**
     * Find follow
     *
     * @param member member
     * @param follow member
     * @return follow
     * @throws IllegalArgumentException Argument is wrong
     */
    public Follow searchOne(Member member, Member follow) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(follow, "Follow");

        return fr.findOne(member, follow);
    }

    /**
     * Find followingList
     *
     * @param member member
     * @return followingList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchFollowingList(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowingList(member);
    }

    /**
     * Find followingList by name
     *
     * @param member member
     * @param name   following name
     * @return followingList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchFollowingListByName(Member member, String name) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowingListByName(member, name);
    }

    /**
     * Find followerList
     *
     * @param member member
     * @return followerList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchFollowerList(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowerList(member);
    }

    /**
     * Find followerList by name
     *
     * @param member member
     * @param name   follower name
     * @return followerList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchFollowerListByName(Member member, String name) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowerListByName(member, name);
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
