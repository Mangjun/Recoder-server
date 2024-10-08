package yuhan.hgcq.server.dto.photo;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LikedDTO implements Serializable {
    private Long photoId;
}
