package yuhan.hgcq.server.dto.member;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Members implements Serializable {
    private List<MemberDTO> memberList;
    private List<MemberDTO> followingList;
}
