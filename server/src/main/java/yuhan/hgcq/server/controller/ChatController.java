package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.chat.ChatDTO;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.ChatService;
import yuhan.hgcq.server.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService cs;
    private final MemberService ms;
    private final AlbumService as;

    @GetMapping("/list/albumId")
    public ResponseEntity<?> listChatsByAlbum(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumId);

                            if (fa != null) {
                                try {
                                    List<Chat> chatList = cs.searchAll(fa);
                                    List<ChatDTO> chatDTOList = new ArrayList<>();

                                    for (Chat chat : chatList) {
                                        ChatDTO dto = mapping(chat);
                                        chatDTOList.add(dto);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(chatDTOList);
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found ChatList Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Album Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    private ChatDTO mapping(Chat chat) {
        ChatDTO dto = new ChatDTO();
        dto.setChatId(chat.getId());
        dto.setMessage(chat.getMessage());
        dto.setTime(chat.getTime().toString());
        dto.setWriterId(chat.getWriter().getId());
        dto.setWriterName(chat.getWriter().getName());
        return dto;
    }
}
