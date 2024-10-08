package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.repository.TeamMemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamMemberService {
    private static final Logger log = LoggerFactory.getLogger(TeamMemberService.class);

    private final TeamMemberRepository tmr;

    /**
     * Invite member
     *
     * @param member     member
     * @param teamMember teamMember
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void inviteMember(Member member, TeamMember teamMember) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isAdmin = isAdmin(member, team);

        if (isAdmin) {
            tmr.save(teamMember);
            log.info("Team Member invite {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Expel Member
     *
     * @param member     member
     * @param teamMember teamMember
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void expelMember(Member member, TeamMember teamMember) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isAdmin = isAdmin(member, team);
        boolean objIsAdmin = teamMember.getIsAdmin();

        if (isAdmin && !objIsAdmin) {
            tmr.delete(teamMember);
            log.info("Team Member expel {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Find member in team
     *
     * @param team   team
     * @param member member
     * @return teamMember
     * @throws IllegalArgumentException Argument is wrong
     */
    public TeamMember searchOne(Team team, Member member) throws IllegalArgumentException {
        ensureNotNull(team, "Team");
        ensureNotNull(member, "Member");

        return tmr.findOne(member, team);
    }

    /**
     * Authorize admin
     *
     * @param member     member
     * @param teamMember teamMember
     * @throws AccessException          Not owner
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void authorizeAdmin(Member member, TeamMember teamMember) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            teamMember.authorizeAdmin();
            tmr.update(teamMember);
            log.info("Team Member authorize admin {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Revoke admin
     *
     * @param member     member
     * @param teamMember teamMember
     * @throws AccessException          Not owner
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void revokeAdmin(Member member, TeamMember teamMember) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            teamMember.revokeAdmin();
            tmr.update(teamMember);
            log.info("Team Member revoke admin {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Find teamList the member has
     *
     * @param member member
     * @return teamList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Team> searchTeamList(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return tmr.findAll(member);
    }

    /**
     * Find by the name of the teamList the member has
     *
     * @param member member
     * @param name   team name
     * @return teamList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Team> searchTeamListByName(Member member, String name) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(name, "Name");

        return tmr.findByName(member, name);
    }

    /**
     * Find memberList in Team
     *
     * @param team team
     * @return memberList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchMemberList(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return tmr.findByTeam(team);
    }

    /**
     * Find adminList in Team
     *
     * @param team team
     * @return adminList
     * @throws IllegalArgumentException Argument is wrong
     */
    public List<Member> searchAdminList(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return tmr.findAdminByTeam(team);
    }

    /**
     * Argument Check if Null
     *
     * @param obj  argument
     * @param name by log
     */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /**
     * Check member is owner
     *
     * @param member member
     * @param team   team
     * @return is owner?
     */
    private boolean isOwner(Member member, Team team) {
        return team.getOwner().equals(member);
    }

    /**
     * Check member is admin
     *
     * @param member member
     * @param team   team
     * @return is admin?
     */
    private boolean isAdmin(Member member, Team team) {
        List<Member> adminList = tmr.findAdminByTeam(team);
        return adminList.contains(member);
    }
}
