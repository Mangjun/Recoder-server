package yuhan.hgcq.server.dto.photo;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhotoDTO implements Serializable {
    private Long photoId;
    private Long albumId;
    private String name;
    private String path;
    private String region;
    private String created;
    private Boolean isLiked;
}
