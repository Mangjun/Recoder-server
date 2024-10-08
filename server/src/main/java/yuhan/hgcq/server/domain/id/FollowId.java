package yuhan.hgcq.server.domain.id;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
public class FollowId implements Serializable {
    private Long memberId;
    private Long followId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowId followId = (FollowId) o;
        return Objects.equals(memberId, followId.memberId) && Objects.equals(this.followId, followId.followId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, followId);
    }
}
