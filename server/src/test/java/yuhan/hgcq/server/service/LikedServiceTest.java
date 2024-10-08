package yuhan.hgcq.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.AccessException;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.*;
import yuhan.hgcq.server.dto.member.SignupForm;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LikedServiceTest {
    @Autowired
    LikedService ls;

    @Autowired
    LocalPhotoService ps;

    @Autowired
    AlbumService as;

    @Autowired
    MemberService ms;

    @Autowired
    TeamService ts;

    Long m1Id;
    Long m2Id;
    Long m3Id;
    Long m4Id;

    Long t1Id;
    Long t2Id;

    Long a1Id;
    Long a2Id;

    Long p1Id;
    Long p2Id;

    @BeforeEach
    void setUp() {
        SignupForm m1 = new SignupForm("m1", "m1@test.com", "1234");
        SignupForm m2 = new SignupForm("m2", "m2@test.com", "1234");
        SignupForm m3 = new SignupForm("m3", "m3@test.com", "1234");
        SignupForm m4 = new SignupForm("m4", "m4@test.com", "1234");

        m1Id = ms.join(m1);
        m2Id = ms.join(m2);
        m3Id = ms.join(m3);
        m4Id = ms.join(m4);

        Member fm1 = ms.searchOne(m1Id);
        Member fm2 = ms.searchOne(m2Id);
        Member fm3 = ms.searchOne(m3Id);
        Member fm4 = ms.searchOne(m4Id);

        Team t1 = new Team(fm1, "t1");
        Team t2 = new Team(fm1, "t2");

        t1Id = ts.createTeam(t1);
        t2Id = ts.createTeam(t2);

        Album a1 = new Album(t1, "a1");
        Album a2 = new Album(t1, "a2");

        try {
            a1Id = as.create(fm1, a1);
            a2Id = as.create(fm1, a2);
        } catch (AccessException e) {
            fail();
        }

        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.now());
        Photo p2 = new Photo(a1, "p2", "/t1/a1/p2", "region", LocalDateTime.now());

        p1Id = ps.savePhoto(p1);
        p2Id = ps.savePhoto(p2);
    }

    @Test
    @DisplayName("좋아요 추가")
    void addLike() {
        Member m1 = ms.searchOne(m1Id);
        Photo p1 = ps.searchOne(p1Id);

        Liked l1 = new Liked(m1, p1);
        ls.addLike(l1);

        Liked find = ls.searchOne(m1, p1);

        assertThat(find).isEqualTo(l1);
        assertThat(find.getIsLiked()).isTrue();
    }

    @Test
    @DisplayName("좋아요 삭제")
    void removeLike() {
        Member m1 = ms.searchOne(m1Id);
        Photo p1 = ps.searchOne(p1Id);

        Liked l1 = new Liked(m1, p1);
        ls.addLike(l1);

        Liked find = ls.searchOne(m1, p1);
        ls.removeLike(find);

        find = ls.searchOne(m1, p1);
        assertThat(find.getIsLiked()).isFalse();
    }

    @Test
    @DisplayName("좋아요한 사진 리스트")
    void photoList() {
        Member m1 = ms.searchOne(m1Id);
        Photo p1 = ps.searchOne(p1Id);
        Photo p2 = ps.searchOne(p2Id);

        Liked l1 = new Liked(m1, p1);
        Liked l2 = new Liked(m1, p2);

        ls.addLike(l1);
        ls.addLike(l2);

        List<Photo> likeList = ls.searchAll(m1);
        assertThat(likeList).hasSize(2).contains(p1, p2);
    }

}