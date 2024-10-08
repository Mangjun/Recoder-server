package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuhan.hgcq.server.domain.id.FollowId;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(FollowId.class)
public class Follow {
    @Id
    @Column(name = "member_id", insertable = false, updatable = false)
    private Long memberId;

    @Id
    @Column(name = "follow_id", insertable = false, updatable = false)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id", insertable = false, updatable = false)
    private Member follow;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (member == null || follow == null) {
            throw new IllegalStateException("Member is null");
        }
    }

    public Follow(Member member, Member follow) {
        if (member == null || follow == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        this.member = member;
        this.follow = follow;
        this.memberId = member.getId();
        this.followId = follow.getId();
    }

    @Override
    public String toString() {
        return "Follow{" +
                "memberId=" + memberId +
                ", followId=" + followId +
                '}';
    }
}
