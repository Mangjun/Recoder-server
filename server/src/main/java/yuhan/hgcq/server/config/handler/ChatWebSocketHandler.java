package yuhan.hgcq.server.config.handler;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.chat.ChatDTO;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.ChatService;
import yuhan.hgcq.server.service.MemberService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService cs;
    private final AlbumService as;
    private final MemberService ms;

    private final Gson gson = new Gson();
    private final Map<Long, Set<WebSocketSession>> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        Long albumId = extractAlbumIdFromUri(uri);

        if (albumId != null) {
            sessions.computeIfAbsent(albumId, k -> new HashSet<>()).add(session);
            log.info("Connection established with albumId: {}", albumId);
        } else {
            log.error("Failed to extract albumId from URI: {}", uri);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatDTO chatDTO = gson.fromJson(payload, ChatDTO.class);
        Long albumId = chatDTO.getAlbumId();

        if (albumId == null) {
            log.error("Received message with null albumId");
            return;
        }

        Album fa = as.searchOne(albumId);
        Member fm = ms.searchOne(chatDTO.getWriterId());
        Chat chat = new Chat(fm, chatDTO.getMessage(), fa);

        cs.create(chat);

        Set<WebSocketSession> sessionsForAlbum = sessions.get(albumId);
        if (sessionsForAlbum != null) {
            for (WebSocketSession s : sessionsForAlbum) {
                if (!s.getId().equals(session.getId())) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uri = session.getUri().toString();
        Long albumId = extractAlbumIdFromUri(uri);

        if (albumId != null) {
            Set<WebSocketSession> sessionsForAlbum = sessions.get(albumId);
            if (sessionsForAlbum != null) {
                sessionsForAlbum.remove(session);
                if (sessionsForAlbum.isEmpty()) {
                    sessions.remove(albumId);
                }
            }
            log.info("Connection closed with albumId: {}", albumId);
        } else {
            log.error("Failed to extract albumId from URI: {}", uri);
        }
    }

    private Long extractAlbumIdFromUri(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.valueOf(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}
