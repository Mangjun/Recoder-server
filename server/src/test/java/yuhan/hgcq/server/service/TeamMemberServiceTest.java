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
class TeamMemberServiceTest {
    @Autowired
    TeamMemberService tms;

    @Autowired
    TeamService ts;

    @Autowired
    MemberService ms;

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
    }

    @Test
    @DisplayName("그룹에 회원 초대")
    void inviteMember() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);

        try {
            tms.inviteMember(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        List<Member> members = tms.searchMemberList(t1);
        assertThat(members).hasSize(2).contains(m1, m2);
    }

    @Test
    @DisplayName("그룹에 회원 초대는 관리자만 할 수 있다")
    void inviteMemberNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);

        try {
            tms.inviteMember(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        TeamMember tm2 = new TeamMember(t1, m3);

        assertThrows(AccessException.class, () -> tms.inviteMember(m2, tm2));
    }

    @Test
    @DisplayName("회원 추방")
    void expelMember() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);

        try {
            tms.inviteMember(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        try {
            tms.expelMember(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        List<Member> members = tms.searchMemberList(t1);
        assertThat(members).hasSize(1).contains(m1);
    }
    
    @Test
    @DisplayName("회원 추방은 관리자만 할 수 있다")
    void expelMemberNotAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(AccessException.class, () -> tms.expelMember(m2, tm2));
    }

    @Test
    @DisplayName("관리자 부여")
    void authAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
            tms.authorizeAdmin(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        List<Member> adminList = tms.searchAdminList(t1);
        assertThat(adminList).hasSize(2).contains(m1, m2);
    }
    
    @Test
    @DisplayName("관리자 권한 부여는 소유자만 할 수 있다")
    void authAdminNotOwner() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
            tms.authorizeAdmin(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(AccessException.class, () -> tms.authorizeAdmin(m2, tm2));
    }
    
    @Test
    @DisplayName("관리자 박탈")
    void revokeAdmin() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
            tms.authorizeAdmin(m1, tm1);
            tms.revokeAdmin(m1, tm1);
        } catch (AccessException e) {
            fail();
        }

        List<Member> adminList = tms.searchAdminList(t1);
        assertThat(adminList).hasSize(1).contains(m1);
    }
    
    @Test
    @DisplayName("관리자 권한 박탈은 소유자만 할 수 있다")
    void revokeAdminNotOwner() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
            tms.authorizeAdmin(m1, tm1);
            tms.authorizeAdmin(m1, tm2);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(AccessException.class, () -> tms.revokeAdmin(m2, tm2));
    }

    @Test
    @DisplayName("회원이 속한 그룹 리스트")
    void teamList() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);
        Member m3 = ms.searchOne(m3Id);

        Team t1 = ts.searchOne(t1Id);
        Team t2 = ts.searchOne(t2Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t2, m2);
        TeamMember tm3 = new TeamMember(t1, m3);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
            tms.inviteMember(m1, tm3);
        } catch (AccessException e) {
            fail();
        }

        List<Team> m2TeamList = tms.searchTeamList(m2);
        List<Team> m3TeamList = tms.searchTeamList(m3);

        assertThat(m2TeamList).hasSize(2).contains(t1, t2);
        assertThat(m3TeamList).hasSize(1).contains(t1);
    }
    
    @Test
    @DisplayName("회원이 속한 그룹 이름으로 검색")
    void teamListByName() {
        Member m1 = ms.searchOne(m1Id);
        Member m2 = ms.searchOne(m2Id);

        Team t1 = ts.searchOne(t1Id);
        Team t2 = ts.searchOne(t2Id);

        TeamMember tm1 = new TeamMember(t1, m2);
        TeamMember tm2 = new TeamMember(t2, m2);

        try {
            tms.inviteMember(m1, tm1);
            tms.inviteMember(m1, tm2);
        } catch (AccessException e) {
            fail();
        }

        List<Team> byt = tms.searchTeamList(m2);
        List<Team> by2 = tms.searchTeamList(m2);

        assertThat(byt).hasSize(2).contains(t1, t2);
        assertThat(by2).hasSize(1).contains(t2);
    }
}