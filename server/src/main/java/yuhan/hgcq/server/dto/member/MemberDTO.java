package yuhan.hgcq.server.dto.member;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO implements Serializable {
    private Long memberId;
    private String name;
    private String email;
    private String image;
}
