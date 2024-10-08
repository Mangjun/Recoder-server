package yuhan.hgcq.server.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yuhan.hgcq.server.dto.member.MemberDTO;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follower implements Serializable {
    private List<MemberDTO> followerList;
    private List<MemberDTO> followingList;
}
