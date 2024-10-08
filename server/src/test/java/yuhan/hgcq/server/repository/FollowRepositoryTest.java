package yuhan.hgcq.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowRepositoryTest {
    @Autowired
    FollowRepository fr;

    @Autowired
    MemberRepository mr;

    Long saveMemberId;
    Long saveMemberId2;
    Long saveMemberId3;
    Long saveMemberId4;

    @BeforeEach
    void setUp() {
        Member memberA = new Member("A1", "a@test.com", "a1");
        Member memberB = new Member("B1", "b@test.com", "b1");
        Member memberC = new Member("C1", "c@test.com", "c1");
        Member memberD = new Member("D1", "d@test.com", "d1");

        saveMemberId = mr.save(memberA);
        saveMemberId2 = mr.save(memberB);
        saveMemberId3 = mr.save(memberC);
        saveMemberId4 = mr.save(memberD);
    }

    @Test
    void save() {
        Member findMember = mr.findOne(saveMemberId);
        Member findMember2 = mr.findOne(saveMemberId2);
        Member findMember3 = mr.findOne(saveMemberId3);
        Member findMember4 = mr.findOne(saveMemberId4);

        Follow follow = new Follow(findMember, findMember2);

        fr.save(follow);

        Follow find = fr.findOne(findMember, findMember2);

        assertThat(find).isEqualTo(follow);
    }

    @Test
    void delete() {
        Member findMember = mr.findOne(saveMemberId);
        Member findMember2 = mr.findOne(saveMemberId2);
        Member findMember3 = mr.findOne(saveMemberId3);
        Member findMember4 = mr.findOne(saveMemberId4);

        Follow follow = new Follow(findMember, findMember2);

        fr.save(follow);

        Follow find = fr.findOne(findMember, findMember2);
        fr.delete(find);

        List<Member> followList = fr.findFollowingList(findMember);

        assertThat(followList).isEmpty();
    }

    @Test
    void findFollowingListByName() {
        Member findMember = mr.findOne(saveMemberId);
        Member findMember2 = mr.findOne(saveMemberId2);
        Member findMember3 = mr.findOne(saveMemberId3);
        Member findMember4 = mr.findOne(saveMemberId4);

        Follow follow1 = new Follow(findMember, findMember2);
        Follow follow2 = new Follow(findMember, findMember3);

        fr.save(follow1);
        fr.save(follow2);

        List<Member> find1 = fr.findFollowingListByName(findMember, "1");
        List<Member> find2 = fr.findFollowingListByName(findMember, "B");

        assertThat(find1).hasSize(2).contains(findMember2, findMember3).doesNotContain(findMember4);
        assertThat(find2).contains(findMember2).doesNotContain(findMember3);
    }
}