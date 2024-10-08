package yuhan.hgcq.server.dto.follow;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowDTO implements Serializable {
    private Long followId;
}
