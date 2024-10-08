package yuhan.hgcq.server.dto.team;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberInTeamDTO implements Serializable {
    private Long memberId;
    private String name;
    private Boolean isAdmin;
    private Boolean isOwner;
}
