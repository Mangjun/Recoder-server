package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.dto.photo.UploadTeamForm;
import yuhan.hgcq.server.dto.team.*;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamMemberService;
import yuhan.hgcq.server.service.TeamService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final MemberService ms;
    private final TeamService ts;
    private final TeamMemberService tms;

    /**
     * Create Team
     *
     * @param createForm team create form
     * @param request    request
     * @return status code
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateForm createForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        Team newTeam = new Team(findMember, createForm.getName());
                        try {
                            ts.createTeam(newTeam);

                            List<Long> memberIdList = createForm.getMembers();
                            List<Member> memberList = new ArrayList<>();

                            for (Long memberId : memberIdList) {
                                try {
                                    Member fm = ms.searchOne(memberId);

                                    if (fm != null) {
                                        memberList.add(fm);
                                    }

                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }

                            for (Member member : memberList) {
                                TeamMember newTeamMember = new TeamMember(newTeam, member);
                                try {
                                    tms.inviteMember(findMember, newTeamMember);
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }

                            return ResponseEntity.status(HttpStatus.CREATED).body("Create Team Success");
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * invite member
     *
     * @param inviteForm invite form
     * @param request    request
     * @return status code
     */
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@RequestBody TeamInviteForm inviteForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        Long teamId = inviteForm.getTeamId();
                        try {
                            Team ft = ts.searchOne(teamId);

                            if (ft != null) {
                                List<Long> memberIdList = inviteForm.getMembers();
                                List<Member> memberList = new ArrayList<>();

                                for (Long memberId : memberIdList) {
                                    try {
                                        Member fm = ms.searchOne(memberId);

                                        if (fm != null) {
                                            memberList.add(fm);
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }

                                for (Member member : memberList) {
                                    TeamMember newTeamMember = new TeamMember(ft, member);
                                    try {
                                        tms.inviteMember(findMember, newTeamMember);
                                    } catch (AccessException e) {
                                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }

                                return ResponseEntity.status(HttpStatus.OK).body("Invite Member In Team Success");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Expel member
     *
     * @param teamMemberDTO teamMember dto
     * @param request       request
     * @return status code
     */
    @PostMapping("/expel")
    public ResponseEntity<?> expelMember(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.searchOne(teamMemberDTO.getMemberId());

                            try {
                                Team ft = ts.searchOne(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                    try {
                                        TeamMember ftm = tms.searchOne(ft, fm);

                                        if (ftm != null) {
                                            try {
                                                tms.expelMember(findMember, ftm);
                                                return ResponseEntity.status(HttpStatus.OK).body("Expel Member Success");
                                            } catch (AccessException e) {
                                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                            }
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Update team
     *
     * @param updateForm team update form
     * @param request    request
     * @return status code
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateTeam(@RequestBody TeamUpdateForm updateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.searchOne(updateForm.getTeamId());

                            if (ft != null) {
                                ft.changeName(updateForm.getName());

                                try {
                                    ts.updateTeam(findMember, ft);
                                    return ResponseEntity.status(HttpStatus.OK).body("Update Team Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Delete team
     *
     * @param teamDTO team dto
     * @param request request
     * @return status code
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.searchOne(teamDTO.getTeamId());

                            if (ft != null) {
                                try {
                                    ts.deleteTeam(findMember, ft);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Team Success");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Authorize admin
     *
     * @param teamMemberDTO teamMember dto
     * @param request       request
     * @return status code
     */
    @PostMapping("/authorize")
    public ResponseEntity<?> authorizeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.searchOne(teamMemberDTO.getMemberId());

                            try {
                                Team ft = ts.searchOne(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                    try {
                                        TeamMember ftm = tms.searchOne(ft, fm);

                                        if (ftm != null) {
                                            try {
                                                tms.authorizeAdmin(findMember, ftm);
                                                return ResponseEntity.status(HttpStatus.OK).body("Authorize Admin Success");
                                            } catch (AccessException e) {
                                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                            }
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Revoke admin
     *
     * @param teamMemberDTO teamMember dto
     * @param request       request
     * @return status code
     */
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.searchOne(teamMemberDTO.getMemberId());
                            try {
                                Team ft = ts.searchOne(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                    try {
                                        TeamMember ftm = tms.searchOne(ft, fm);

                                        if (ftm != null) {
                                            try {
                                                tms.revokeAdmin(findMember, ftm);
                                                return ResponseEntity.status(HttpStatus.OK).body("Revoke Admin Success");
                                            } catch (AccessException e) {
                                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                            }
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find teamList
     *
     * @param request request
     * @return status code, teamList
     */
    @GetMapping("/list")
    public ResponseEntity<?> teamListByMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Team> teamList = tms.searchTeamList(findMember);
                            List<TeamDTO> teamDTOList = new ArrayList<>();

                            for (Team team : teamList) {
                                TeamDTO teamDTO = mapping(team);
                                teamDTOList.add(teamDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find teamList by name
     *
     * @param name    team name
     * @param request request
     * @return status code, teamList
     */
    @GetMapping("/list/name")
    public ResponseEntity<?> searchTeamByTeamName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Team> teamList = tms.searchTeamListByName(findMember, name);
                            List<TeamDTO> teamDTOList = new ArrayList<>();

                            for (Team team : teamList) {
                                TeamDTO teamDTO = mapping(team);
                                teamDTOList.add(teamDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find memberList
     *
     * @param teamId  teamId
     * @param request request
     * @return status code, memberList
     */
    @GetMapping("/memberlist/teamId")
    public ResponseEntity<?> memberListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.searchOne(teamId);

                            if (ft != null) {
                                Member owner = ft.getOwner();
                                try {
                                    List<Team> teamList = tms.searchTeamList(findMember);

                                    if (teamList.contains(ft)) {
                                        try {
                                            List<Member> memberList = tms.searchMemberList(ft);
                                            try {
                                                List<Member> adminList = tms.searchAdminList(ft);
                                                List<MemberInTeamDTO> memberDTOList = new ArrayList<>();

                                                for (Member member : memberList) {
                                                    MemberInTeamDTO memberDTO = mapping(member);

                                                    if (owner.equals(member)) {
                                                        memberDTO.setIsOwner(true);
                                                    } else {
                                                        memberDTO.setIsOwner(false);
                                                    }

                                                    if (adminList.contains(member)) {
                                                        memberDTO.setIsAdmin(true);
                                                    } else {
                                                        memberDTO.setIsAdmin(false);
                                                    }

                                                    memberDTOList.add(memberDTO);
                                                }

                                                return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                            }
                                        } catch (IllegalArgumentException e) {
                                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                        }
                                    }
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find adminList
     *
     * @param teamId  teamId
     * @param request request
     * @return status code, adminList
     */
    @GetMapping("/adminlist/teamId")
    public ResponseEntity<?> adminListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.searchOne(teamId);

                            if (ft != null) {
                                try {
                                    List<Member> adminList = tms.searchAdminList(ft);
                                    List<Long> memberDTOList = new ArrayList<>();

                                    for (Member member : adminList) {
                                        memberDTOList.add(member.getId());
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Upload team image
     *
     * @param form    team image form
     * @param request request
     * @return status code
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@ModelAttribute UploadTeamForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            ts.uploadTeamImage(findMember, form);
                            return ResponseEntity.status(HttpStatus.OK).body("Upload Team Success");
                        } catch (AccessException e) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    private TeamDTO mapping(Team team) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setOwner(team.getOwner().getName());
        teamDTO.setImage(team.getImage());
        return teamDTO;
    }

    private MemberInTeamDTO mapping(Member member) {
        MemberInTeamDTO memberDTO = new MemberInTeamDTO();
        memberDTO.setMemberId(member.getId());
        memberDTO.setName(member.getName());
        return memberDTO;
    }
}
