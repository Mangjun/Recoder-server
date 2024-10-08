package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import yuhan.hgcq.server.domain.id.TeamMemberId;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TeamMemberId.class)
public class TeamMember {
    @Id
    @Column(name = "team_id")
    private Long teamId;

    @Id
    @Column(name = "member_id")
    private Long memberId;

    private Boolean isAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    public TeamMember(Team team, Member member) {
        if (team == null || member == null) {
            throw new IllegalArgumentException("Team and Member cannot be null");
        }
        this.team = team;
        this.member = member;
        this.teamId = team.getId();
        this.memberId = member.getId();
        this.isAdmin = false;
    }

    public void authorizeAdmin() {
        this.isAdmin = true;
    }

    public void revokeAdmin() {
        this.isAdmin = false;
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "teamId=" + teamId +
                ", memberId=" + memberId +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
