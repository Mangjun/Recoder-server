package yuhan.hgcq.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamMemberRepositoryTest {
    @Autowired
    TeamMemberRepository teamMemberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    Long saveMemberId;
    Long saveTeamId;

    @BeforeEach
    void setUp() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberC = new Member("C", "c@test.com", "c1");
        Member memberD = new Member("D", "d@test.com", "d1");

        saveMemberId = memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);

        Member findMember = memberRepository.findOne(saveMemberId);

        Team t1 = new Team(findMember, "G1");
        Team t2 = new Team(findMember, "G2");
        Team t3 = new Team(findMember, "G3");
        Team t4 = new Team(findMember, "G4");

        saveTeamId = teamRepository.save(t1);
        teamRepository.save(t2);
        teamRepository.save(t3);
        teamRepository.save(t4);

        teamMemberRepository.save(new TeamMember(t1, findMember));
        teamMemberRepository.save(new TeamMember(t2, findMember));
        teamMemberRepository.save(new TeamMember(t3, findMember));
        teamMemberRepository.save(new TeamMember(t4, findMember));
    }

    @Test
    void save() {
        Team findTeam = teamRepository.findOne(saveTeamId);
        Member findA = memberRepository.findOne(saveMemberId);
        Member findB = memberRepository.findOne("b@test.com");

        TeamMember tm = new TeamMember(findTeam, findB);
        teamMemberRepository.save(tm);

        List<Member> members = teamMemberRepository.findByTeam(findTeam);

        assertThat(members).hasSize(2);
        assertThat(members).contains(findA, findB);
    }

    @Test
    void delete() {
        Team findTeam = teamRepository.findOne(saveTeamId);
        Member findA = memberRepository.findOne(saveMemberId);
        Member findB = memberRepository.findOne("b@test.com");

        TeamMember tm = new TeamMember(findTeam, findB);
        teamMemberRepository.save(tm);

        teamMemberRepository.delete(tm);

        List<Member> members = teamMemberRepository.findByTeam(findTeam);

        assertThat(members).hasSize(1);
        assertThat(members).contains(findA);
    }

    @Test
    void deleteAll() {
        Team findTeam = teamRepository.findOne(saveTeamId);
        Member findA = memberRepository.findOne(saveMemberId);
        Member findB = memberRepository.findOne("b@test.com");

        TeamMember tm = new TeamMember(findTeam, findB);
        teamMemberRepository.save(tm);

        teamMemberRepository.deleteAll(findTeam);
        List<Member> members = teamMemberRepository.findByTeam(findTeam);

        assertThat(members).hasSize(0);
    }

    @Test
    void findAll() {
        Member findA = memberRepository.findOne(saveMemberId);

        List<Team> findList = teamMemberRepository.findAll(findA);

        assertThat(findList).hasSize(4);
    }

    @Test
    void findByName() {
        Member findA = memberRepository.findOne(saveMemberId);
        Team findG = teamRepository.findOne(saveTeamId);

        List<Team> find = teamMemberRepository.findByName(findA, "G1");
        List<Team> finds = teamMemberRepository.findByName(findA, "G");

        assertThat(find).hasSize(1);
        assertThat(find).contains(findG);

        assertThat(finds).hasSize(4);
    }
}