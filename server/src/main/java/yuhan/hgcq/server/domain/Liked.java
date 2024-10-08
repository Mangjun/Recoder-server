package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Liked {
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @Id
    @Column(name = "photo_id")
    private Long photoId;

    private Boolean isLiked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private Photo photo;

    public Liked(Member member, Photo photo) {
        if (member == null || photo == null) {
            throw new IllegalArgumentException("Member and Photo cannot be null");
        }
        this.member = member;
        this.photo = photo;
        this.memberId = member.getId();
        this.photoId = photo.getId();
        this.isLiked = true;
    }

    public void cancelLiked() {
        this.isLiked = false;
    }

    public void addLiked() {
        this.isLiked = true;
    }

    @Override
    public String toString() {
        return "Liked{" +
                "memberId=" + memberId +
                ", photoId=" + photoId +
                ", isLiked=" + isLiked +
                '}';
    }
}
