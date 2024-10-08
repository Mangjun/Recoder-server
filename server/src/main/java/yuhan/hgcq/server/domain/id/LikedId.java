package yuhan.hgcq.server.domain.id;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class LikedId {
    private Long memberId;
    private Long photoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikedId likedId = (LikedId) o;
        return Objects.equals(memberId, likedId.memberId) && Objects.equals(photoId, likedId.photoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, photoId);
    }
}
