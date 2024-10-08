package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumUpdateForm implements Serializable {
    private Long albumId;
    private String name;
}
