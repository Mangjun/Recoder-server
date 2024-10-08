package yuhan.hgcq.server.dto.chat;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateChatForm implements Serializable {
    private Long albumId;
    private String message;
}
