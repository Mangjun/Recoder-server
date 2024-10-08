package yuhan.hgcq.server.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberC = new Member("C", "c@test.com", "c1");
        Member memberD = new Member("D", "d@test.com", "d1");

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);
    }

    @Test
    void save() {
        Member owner = memberRepository.findOne("a@test.com");

        Team team = new Team(owner, "G1");
        Long saveId = teamRepository.save(team);

        Team find = teamRepository.findOne(saveId);

        assertThat(find).isEqualTo(team);
    }

    @Test
    void delete() {
        Member owner = memberRepository.findOne("a@test.com");

        Team team = new Team(owner, "G1");
        Long saveId = teamRepository.save(team);
        teamRepository.delete(saveId);

        Team find = teamRepository.findOne(saveId);
        assertThat(find).isNull();
    }
}