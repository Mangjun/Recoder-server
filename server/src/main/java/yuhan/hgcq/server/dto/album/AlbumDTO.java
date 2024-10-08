package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumDTO implements Serializable {
    private Long albumId;
    private Long teamId;
    private String name;
}
