package yuhan.hgcq.server.dto.team;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamInviteForm implements Serializable {
    private Long teamId;
    private List<Long> members = new ArrayList<>();
}
