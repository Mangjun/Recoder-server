package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.repository.ChatRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatRepository cr;

    /**
     * Create chat
     *
     * @param chat chat
     * @return chatId
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public Long create(Chat chat) throws IllegalArgumentException {
        ensureNotNull(chat, "Chat");

        Long saveId = cr.save(chat);
        log.info("Create Chat : {}", chat);
        return saveId;
    }

    /**
     * Delete chat
     *
     * @param member member
     * @param chat   chat
     * @throws AccessException          Not writer
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void delete(Member member, Chat chat) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(chat, "Chat");

        Member writer = chat.getWriter();
        boolean isWriter = Objects.equals(member.getId(), writer.getId());

        if (isWriter) {
            cr.delete(chat.getId());
            log.info("Delete Chat : {}", chat);
        } else {
            throw new AccessException("Not Writer");
        }
    }

    /**
     * Find chat
     *
     * @param id chatId
     * @return chat
     * @throws IllegalArgumentException Argument is wrong
     */
    public Chat searchOne(Long id) throws IllegalArgumentException {
        Chat find = cr.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("Chat not found");
        }

        return find;
    }

    /**
     * Find chattingList
     *
     * @param album album
     * @return chattingList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Chat> searchAll(Album album) throws IllegalArgumentException {
        ensureNotNull(album, "Album");

        return cr.findAll(album);
    }

    /**
     * Argument Check if Null
     *
     * @param obj  argument
     * @param name by log
     */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
