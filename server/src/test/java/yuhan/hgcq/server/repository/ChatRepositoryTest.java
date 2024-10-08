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
class ChatRepositoryTest {
    @Autowired
    ChatRepository cr;

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
        Member findMember = mr.findOne(saveMemberId);
        Album findAlbum = ar.findOne(saveAlbumId);
        Chat chat = new Chat(findMember, "test", findAlbum);

        Long saveId = cr.save(chat);

        Chat findChat = cr.findOne(saveId);

        assertThat(findChat).isEqualTo(chat);
    }

    @Test
    void delete() {
        Member findMember = mr.findOne(saveMemberId);
        Album findAlbum = ar.findOne(saveAlbumId);
        Chat chat = new Chat(findMember, "test", findAlbum);

        Long saveId = cr.save(chat);

        cr.delete(saveId);

        Chat findChat = cr.findOne(saveId);

        assertThat(findChat).isNull();
    }

    @Test
    void findAll() {
        Member findMember = mr.findOne(saveMemberId);
        Member findMember2 = mr.findOne(saveMemberId2);
        Member findMember3 = mr.findOne(saveMemberId3);
        Member findMember4 = mr.findOne(saveMemberId4);

        Album findAlbum = ar.findOne(saveAlbumId);

        Chat chat = new Chat(findMember, "test", findAlbum);
        Chat chat2 = new Chat(findMember2, "test2", findAlbum);
        Chat chat3 = new Chat(findMember3, "test3", findAlbum);
        Chat chat4 = new Chat(findMember4, "test4", findAlbum);

        cr.save(chat);
        cr.save(chat2);
        cr.save(chat3);

        List<Chat> chats = cr.findAll(findAlbum);
        assertThat(chats).hasSize(3).contains(chat, chat2, chat3).doesNotContain(chat4);
    }
}