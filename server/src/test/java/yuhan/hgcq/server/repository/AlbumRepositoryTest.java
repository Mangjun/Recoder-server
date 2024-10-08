package yuhan.hgcq.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumRepositoryTest {
    @Autowired
    MemberRepository mr;

    @Autowired
    AlbumRepository ar;

    @Autowired
    TeamRepository tr;

    @Autowired
    TeamMemberRepository tmr;

    Long saveMemberId;
    Long saveMemberId2;
    Long saveMemberId3;
    Long saveMemberId4;

    Long saveTeamId;

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
    }

    @Test
    void save() {
        Team team = tr.findOne(saveTeamId);
        Album album = new Album(team, "a1");
        Long saveId = ar.save(album);

        Album find = ar.findOne(saveId);
        assertThat(find).isEqualTo(album);
    }

    @Test
    void delete() {
        Team team = tr.findOne(saveTeamId);
        Album album = new Album(team, "a1");
        Long saveId = ar.save(album);

        ar.delete(saveId);

        Album find = ar.findOne(saveId);

        assertThat(find).isNull();
    }
}