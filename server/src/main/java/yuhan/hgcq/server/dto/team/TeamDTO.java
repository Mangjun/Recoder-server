package yuhan.hgcq.server.dto.team;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamDTO implements Serializable {
    private Long teamId;
    private String owner;
    private String name;
    private String image;
}
