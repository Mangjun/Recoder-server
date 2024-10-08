package yuhan.hgcq.server.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {
        Member member = new Member("A", "a@test.com", "a1");

        Long saveId = memberRepository.save(member);

        Member findMember = memberRepository.findOne(saveId);

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void delete() {
        Member member = new Member("A", "a@test.com", "a1");

        Long saveId = memberRepository.save(member);
        memberRepository.delete(saveId);

        Member findMember = memberRepository.findOne(saveId);

        assertThat(findMember).isNull();
    }

    @Test
    void findEmail() {
        Member member = new Member("A", "a@test.com", "a1");

        memberRepository.save(member);

        Member findMember = memberRepository.findOne("a@test.com");

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findAll() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberC = new Member("C", "c@test.com", "c1");

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<Member> members = memberRepository.findAll();

        assertThat(members).hasSize(3);
        assertThat(members).contains(memberA, memberB, memberC);
    }

    @Test
    void findName() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberC = new Member("C", "b2@test.com", "c1");
        Member memberD = new Member("D", "c@test.com", "d1");

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);

        List<Member> find = memberRepository.findByName("B");
        assertThat(find).hasSize(1).contains(memberB);
    }

    @Test
    void findAllEmails() {
        Member memberA = new Member("A", "a@test.com", "a1");
        Member memberB = new Member("B", "b@test.com", "b1");
        Member memberB2 = new Member("C", "b2@test.com", "c1");
        Member memberC = new Member("D", "c@test.com", "d1");

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberB2);
        memberRepository.save(memberC);

        List<String> emails = memberRepository.findAllEmails();
        assertThat(emails).hasSize(4);
        assertThat(emails).contains(memberA.getEmail(), memberB.getEmail(), memberC.getEmail(), memberB2.getEmail());
    }
}