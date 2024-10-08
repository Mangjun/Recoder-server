package yuhan.hgcq.server.dto.team;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamCreateForm implements Serializable {
    private String name;
    private List<Long> members = new ArrayList<Long>();
}
