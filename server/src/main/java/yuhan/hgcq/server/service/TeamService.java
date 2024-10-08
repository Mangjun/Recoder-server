package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.photo.UploadTeamForm;
import yuhan.hgcq.server.repository.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository tr;
    private final TeamMemberRepository tmr;
    private final AlbumRepository ar;
    private final LikedRepository lr;
    private final ChatRepository cr;
    private final PhotoRepository pr;

    private final static String DIRECTORY_PATH = File.separator
            + "app" + File.separator
            + "images" + File.separator
            + "team" + File.separator;

    /**
     * Create Team
     *
     * @param team team
     * @return TeamId
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public Long createTeam(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        Long saveId = tr.save(team);

        TeamMember tm = new TeamMember(team, team.getOwner());
        tm.authorizeAdmin();

        tmr.save(tm);
        log.info("Team created: {}", team);
        return saveId;
    }

    /**
     * Update team information
     *
     * @param member member
     * @param team   team
     * @throws AccessException          Not admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void updateTeam(Member member, Team team) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(team, "Team");

        List<Member> adminList = tmr.findAdminByTeam(team);

        if (adminList.contains(member)) {
            tr.save(team);
            log.info("Team updated: {}", team);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * Delete team or Exit team
     *
     * @param member member
     * @param team   team
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void deleteTeam(Member member, Team team) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(team, "Team");

        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            lr.deleteByTeam(team);
            pr.deleteByTeam(team);
            cr.deleteByTeam(team);
            ar.deleteByTeam(team);
            tmr.deleteAll(team);
            tr.delete(team.getId());
            log.info("Team deleted: {}", team);
        } else {
            TeamMember find = tmr.findOne(member, team);
            tmr.delete(find);
            log.info("Team exit : {}", find);
        }
    }

    /**
     * Find team by teamId
     *
     * @param id teamId
     * @return Team
     * @throws IllegalArgumentException Argument is wrong
     */
    public Team searchOne(Long id) throws IllegalArgumentException {
        Team findTeam = tr.findOne(id);

        if (findTeam == null) {
            throw new IllegalArgumentException("Team not found");
        }

        return findTeam;
    }

    /**
     * Upload team image
     *
     * @param member member
     * @param form   Team Image
     * @throws IOException              Upload error
     * @throws AccessException          Not Admin
     * @throws IllegalArgumentException Argument is wrong
     */
    @Transactional
    public void uploadTeamImage(Member member, UploadTeamForm form) throws IOException, AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");

        Long teamId = form.getTeamId();
        MultipartFile file = form.getFile();
        Team ft = tr.findOne(teamId);

        ensureNotNull(ft, "Team");
        List<Member> adminList = tmr.findAdminByTeam(ft);

        if (adminList.contains(member)) {
            try {
                String newPath = DIRECTORY_PATH + teamId + File.separator;
                File directory = new File(newPath);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String name = file.getOriginalFilename();

                Path path = Paths.get(newPath + name);
                file.transferTo(path);
                String imagePath = "/images/team/" + teamId + "/" + name;

                ft.changeImage(imagePath);
                tr.save(ft);

                log.info("Upload Team Image : {}", imagePath);
            } catch (IOException e) {
                log.error("Upload Team Image Error");
                throw new IOException();
            }
        } else {
            throw new AccessException("Not admin");
        }
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
}
