package yuhan.hgcq.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.AccessException;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.member.SignupForm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LocalPhotoServiceTest {
    @Autowired
    LocalPhotoService ps;

    @Autowired
    MemberService ms;

    @Autowired
    TeamService ts;

    @Autowired
    AlbumService as;

    Long m1Id;
    Long m2Id;
    Long m3Id;
    Long m4Id;

    Long t1Id;
    Long t2Id;

    Long a1Id;
    Long a2Id;

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
    }

    @Test
    @DisplayName("사진 저장")
    void savePhoto() {
        Album a1 = as.searchOne(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.now());

        Long saveId = ps.savePhoto(p1);
        Photo find = ps.searchOne(saveId);

        assertThat(find).isEqualTo(p1);
    }

    @Test
    @DisplayName("사진 삭제")
    void deletePhoto() {
        Album a1 = as.searchOne(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.now());

        Long saveId = ps.savePhoto(p1);
        Photo find = ps.searchOne(saveId);

        ps.deletePhoto(find);

        find = ps.searchOne(saveId);
        assertThat(find.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("사진 삭제 취소")
    void cancelDeletePhoto() {
        Album a1 = as.searchOne(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.now());

        Long saveId = ps.savePhoto(p1);
        Photo find = ps.searchOne(saveId);

        ps.deletePhoto(find);
        find = ps.searchOne(saveId);

        ps.deleteCancelPhoto(find);
        find = ps.searchOne(saveId);

        assertThat(find.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("휴지통 사진 삭제")
    void trash() {
        Album a1 = as.searchOne(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        Long saveId = ps.savePhoto(p1);
        Photo find = ps.searchOne(saveId);

        ps.deletePhoto(find);
        find = ps.searchOne(saveId);
        find.test(LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        List<Photo> trashList = ps.searchTrashList(a1);
        ps.trash(trashList);

        assertThrows(IllegalArgumentException.class, () -> ps.searchOne(saveId));
    }

    @Test
    @DisplayName("경로로 사진 검색")
    void searchOneByPath() {
        Album a1 = as.searchOne(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        Long saveId = ps.savePhoto(p1);
        Photo find = ps.searchOne("/t1/a1/p1");

        assertThat(find).isEqualTo(p1);
    }

    @Test
    @DisplayName("앨범 이동")
    void move() {
        Album a1 = as.searchOne(a1Id);
        Album a2 = as.searchOne(a2Id);

        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", "region", LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        Long saveId = ps.savePhoto(p1);

        List<Photo> photoList = new ArrayList<>();
        photoList.add(p1);

        ps.move(a2, photoList);

        assertThat(p1.getAlbum()).isEqualTo(a2);
    }
}