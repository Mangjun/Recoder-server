package yuhan.hgcq.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PhotoRepositoryTest {
    @Autowired
    MemberRepository mr;

    @Autowired
    AlbumRepository ar;

    @Autowired
    TeamRepository tr;

    @Autowired
    TeamMemberRepository tmr;

    @Autowired
    PhotoRepository pr;

    Long saveMemberId;
    Long saveMemberId2;
    Long saveMemberId3;
    Long saveMemberId4;

    Long saveTeamId;
    Long saveAlbumId;

    @BeforeEach
    void setUp() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberC = new Member("C", "c@test.com", "c1");
        Member memberD = new Member("D", "d@test.com", "d1");

        saveMemberId = mr.save(memberA);
        saveMemberId2 = mr.save(memberB);
        saveMemberId3 = mr.save(memberC);
        saveMemberId4 = mr.save(memberD);

        Member findMember = mr.findOne(saveMemberId);

        Team t1 = new Team(findMember, "G1");

        saveTeamId = tr.save(t1);

        TeamMember tm1 = new TeamMember(t1, memberB);
        TeamMember tm2 = new TeamMember(t1, memberC);
        TeamMember tm3 = new TeamMember(t1, memberD);

        tmr.save(tm1);
        tmr.save(tm2);
        tmr.save(tm3);

        Album album = new Album(t1, "a1");
        saveAlbumId = ar.save(album);
    }

    @Test
    void save() {
        Album findAlbum = ar.findOne(saveAlbumId);

        Photo p1 = new Photo(findAlbum, "p1", "/test/a1", "region", LocalDateTime.now());

        Long saveId = pr.save(p1);

        Photo find = pr.findOne(saveId);

        assertThat(find).isEqualTo(p1);
    }

    @Test
    void delete() {
        Album findAlbum = ar.findOne(saveAlbumId);

        Photo p1 = new Photo(findAlbum, "p1", "/test/a1", "region", LocalDateTime.now());

        Long saveId = pr.save(p1);

        pr.delete(saveId);

        Photo find = pr.findOne(saveId);

        assertThat(find).isNull();
    }

    @Test
    void findByPath() {
        Album findAlbum = ar.findOne(saveAlbumId);

        Photo p1 = new Photo(findAlbum, "p1", "/test/a1", "region", LocalDateTime.now());

        Long saveId = pr.save(p1);

        Photo find = pr.findByPath("/test/a1");

        assertThat(find).isEqualTo(p1);
    }

    @Test
    void findAll() {
        Album findAlbum = ar.findOne(saveAlbumId);

        Photo p1 = new Photo(findAlbum, "p1", "/test/a1", "region", LocalDateTime.now());
        Photo p2 = new Photo(findAlbum, "p2", "/test/a2", "region", LocalDateTime.now());
        Photo p3 = new Photo(findAlbum, "p3", "/test/a3", "region", LocalDateTime.now());
        Photo p4 = new Photo(findAlbum, "p4", "/test/a4", "region", LocalDateTime.now());

        pr.save(p1);
        pr.save(p2);
        pr.save(p3);

        List<Photo> find = pr.findAll(findAlbum);

        assertThat(find).hasSize(3).contains(p1, p2, p3).doesNotContain(p4);
    }

    @Test
    void deleteAll() {
        Album findAlbum = ar.findOne(saveAlbumId);

        Photo p1 = new Photo(findAlbum, "p1", "/test/a1", "region", LocalDateTime.now());
        Photo p2 = new Photo(findAlbum, "p2", "/test/a2", "region", LocalDateTime.now());
        Photo p3 = new Photo(findAlbum, "p3", "/test/a3", "region", LocalDateTime.now());

        pr.save(p1);
        pr.save(p2);
        pr.save(p3);

        pr.deleteAll(findAlbum);

        List<Photo> find = pr.findAll(findAlbum);

        assertThat(find).isEmpty();
    }
}