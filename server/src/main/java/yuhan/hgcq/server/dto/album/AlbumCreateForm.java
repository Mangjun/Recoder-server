package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumCreateForm implements Serializable {
    private Long teamId;
    private String name;
}
