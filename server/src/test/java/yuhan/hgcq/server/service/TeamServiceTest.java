package yuhan.hgcq.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.AccessException;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.member.SignupForm;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamServiceTest {
    @Autowired
    TeamService ts;

    @Autowired
    MemberService ms;

    @Autowired
    TeamMemberService tms;

    Long m1Id;
    Long m2Id;

    @BeforeEach
    void setUp() {
        SignupForm m1 = new SignupForm("m1", "m1@test.com", "1234");
        SignupForm m2 = new SignupForm("m2", "m2@test.com", "1234");

        m1Id = ms.join(m1);
        m2Id = ms.join(m2);

        Member fm1 = ms.searchOne(m1Id);
        Member fm2 = ms.searchOne(m2Id);
    }

    @Test
    @DisplayName("그룹 생성")
    void createTeam() {
        Member findMember = ms.searchOne(m1Id);

        Team t1 = new Team(findMember, "t1");

        Long t1Id = ts.createTeam(t1);
        Team findTeam = ts.searchOne(t1Id);

        assertThat(findTeam).isEqualTo(t1);
    }

    @Test
    @DisplayName("그룹 수정")
    void updateTeam() {
        Member findMember = ms.searchOne(m1Id);

        Team t1 = new Team(findMember, "t1");

        Long t1Id = ts.createTeam(t1);
        Team findTeam = ts.searchOne(t1Id);

        findTeam.changeName("newT1");

        try {
            ts.updateTeam(findMember, findTeam);
        } catch (AccessException e) {
            fail();
        }

        Team updateTeam = ts.searchOne(t1Id);
        assertThat(updateTeam.getName()).isEqualTo("newT1");
    }

    @Test
    @DisplayName("관리자만 그룹 수정이 가능함")
    void updateTeamByAdmin() {
        Member findMember = ms.searchOne(m1Id);
        Member notAdmin = ms.searchOne(m2Id);

        Team t1 = new Team(findMember, "t1");

        Long t1Id = ts.createTeam(t1);
        Team findTeam = ts.searchOne(t1Id);

        findTeam.changeName("newT1");

        assertThrows(AccessException.class, () -> ts.updateTeam(notAdmin, findTeam));
    }

    @Test
    @DisplayName("그룹 삭제")
    void deleteTeam() {
        Member findMember = ms.searchOne(m1Id);

        Team t1 = new Team(findMember, "t1");

        Long t1Id = ts.createTeam(t1);
        Team findTeam = ts.searchOne(t1Id);

        ts.deleteTeam(findMember, findTeam);

        assertThrows(IllegalArgumentException.class, () -> ts.searchOne(t1Id));
    }

    @Test
    @DisplayName("그룹 소유자만 그룹을 삭제할 수 있다")
    void deleteTeamByAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);

        Team t1 = new Team(m1, "t1");
        Long t1Id = ts.createTeam(t1);

        TeamMember tm1 = new TeamMember(t1, m2);

        try {
            tms.inviteMember(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        Team findTeam = ts.searchOne(t1Id);

        ts.deleteTeam(m2, findTeam);
        findTeam = ts.searchOne(t1Id);
        List<Team> teamList = tms.searchTeamList(m2);

        assertThat(findTeam).isEqualTo(t1);
        assertThat(teamList).isEmpty();
    }
}