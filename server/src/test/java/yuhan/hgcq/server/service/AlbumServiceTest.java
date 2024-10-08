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
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.member.SignupForm;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {
    @Autowired
    AlbumService as;

    @Autowired
    MemberService ms;

    @Autowired
    TeamService ts;

    @Autowired
    TeamMemberService tms;

    Long m1Id;
    Long m2Id;
    Long m3Id;
    Long m4Id;

    Long t1Id;
    Long t2Id;

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

        TeamMember tm1 = new TeamMember(t1, fm2);
        TeamMember tm2 = new TeamMember(t1, fm3);
        TeamMember tm3 = new TeamMember(t2, fm4);

        try {
            tms.inviteMember(fm1, tm1);
            tms.inviteMember(fm1, tm2);
            tms.inviteMember(fm1, tm3);
        } catch (AccessException e) {
            fail();
        }
    }

    @Test
    @DisplayName("앨범 생성")
    void create() {
        Member m1 = ms.searchOne(m1Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        Album find = as.searchOne(a1Id);
        assertThat(find).isEqualTo(a1);
    }

    @Test
    @DisplayName("앨범 생성은 관리자만 가능하다")
    void createNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);

        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        assertThrows(AccessException.class, () -> as.create(m2, a1));
    }

    @Test
    @DisplayName("앨범 삭제")
    void deleteAlbum() {
        Member m1 = ms.searchOne(m1Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
            as.deleteAlbum(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        Album find = as.searchOne(a1Id);

        assertThat(find.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("앨범 삭제는 관리자만 할 수 있다")
    void deleteAlbumNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(AccessException.class, () -> as.deleteAlbum(m2, a1));
    }

    @Test
    @DisplayName("앨범 삭제 취소")
    void deleteAlbumCancel() {
        Member m1 = ms.searchOne(m1Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
            as.deleteAlbum(m1, a1);
            as.deleteAlbumCancel(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        Album find = as.searchOne(a1Id);

        assertThat(find.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("앨범 삭제 취소는 관리자만 할 수 있다")
    void deleteAlbumCancelNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
            as.deleteAlbum(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(AccessException.class, () -> as.deleteAlbumCancel(m2, a1));
    }

    @Test
    @DisplayName("휴지통 자동 삭제")
    void trash() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
            Album find = as.searchOne(a1Id);
            find.test(LocalDate.of(2024, 8, 1));

            List<Album> trashList = as.searchAlbumTrashList(t1);
            as.trash(trashList);

            Long finalA1Id = a1Id;
            assertThrows(IllegalArgumentException.class, () -> as.searchOne(finalA1Id));
        } catch (AccessException e) {
            fail();
        }
    }
    
    @Test
    @DisplayName("앨범 수정")
    void update() {
        Member m1 = ms.searchOne(m1Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
            Album find = as.searchOne(a1Id);
            find.changeName("modifyA1");
            as.modify(m1, find);
        } catch (AccessException e) {
            fail();
        }

        Album find = as.searchOne(a1Id);
        assertThat(find.getName()).isEqualTo("modifyA1");
    }

    @Test
    @DisplayName("앨범 수정은 관리자만 할 수 있다")
    void updateNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");

        Long a1Id = null;

        try {
            a1Id = as.create(m1, a1);
        } catch (AccessException e) {
            fail();
        }

        Album find = as.searchOne(a1Id);
        find.changeName("modifyA1");

        assertThrows(AccessException.class, () -> as.modify(m2, find));
    }

    @Test
    @DisplayName("앨범 리스트")
    void albumList() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");
        Album a2 = new Album(t1, "a2");

        Long a1Id = null;
        Long a2Id = null;

        try {
            a1Id = as.create(m1, a1);
            a2Id = as.create(m1, a2);
        } catch (AccessException e) {
            fail();
        }

        List<Album> albumList = as.searchAll(t1);
        assertThat(albumList).hasSize(2).contains(a1, a2);
    }

    @Test
    @DisplayName("앨범 리스트 이름으로 검색")
    void albumListSearchOneByName() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Team t1 = ts.searchOne(t1Id);

        Album a1 = new Album(t1, "a1");
        Album a2 = new Album(t1, "a2");

        Long a1Id = null;
        Long a2Id = null;

        try {
            a1Id = as.create(m1, a1);
            a2Id = as.create(m1, a2);
        } catch (AccessException e) {
            fail();
        }

        List<Album> al1 = as.searchByName(t1, "a");
        List<Album> al2 = as.searchByName(t1, "1");

        assertThat(al1).hasSize(2).contains(a1, a2);
        assertThat(al2).hasSize(1).contains(a1);
    }
}