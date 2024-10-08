package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.follow.FollowDTO;
import yuhan.hgcq.server.dto.follow.Follower;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.FollowService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamMemberService;
import yuhan.hgcq.server.service.TeamService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService fs;
    private final MemberService ms;
    private final TeamService ts;
    private final TeamMemberService tms;

    /**
     * Add Following
     *
     * @param dto     follow dto
     * @param request request
     * @return status code
     */
    @PostMapping("/add")
    public ResponseEntity<?> addFollowing(@RequestBody FollowDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        Long followId = dto.getFollowId();

                        try {
                            Member follow = ms.searchOne(followId);

                            if (follow != null) {
                                Follow following = new Follow(findMember, follow);
                                try {
                                    fs.addFollow(following);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Add Following Success");
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
     * Delete following
     *
     * @param dto     follow dto
     * @param request request
     * @return status code
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFollowing(@RequestBody FollowDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        Long followId = dto.getFollowId();

                        try {
                            Member follow = ms.searchOne(followId);

                            if (follow != null) {
                                Follow ff = fs.searchOne(findMember, follow);

                                if (ff != null) {
                                    try {
                                        fs.removeFollow(ff);
                                        return ResponseEntity.status(HttpStatus.OK).body("Delete Following Success");
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
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
     * Find followingList
     *
     * @param request request
     * @return status code, followingList
     */
    @GetMapping("/followinglist")
    public ResponseEntity<?> followingList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowingList(findMember);
                            List<MemberDTO> dtoList = new ArrayList<>();

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                dtoList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
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
     * Find followingList by name
     *
     * @param name    following name
     * @param request request
     * @return status code, followingList
     */
    @GetMapping("/followinglist/name")
    public ResponseEntity<?> searchFollowingListByName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowingListByName(findMember, name);
                            List<MemberDTO> dtoList = new ArrayList<>();

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                dtoList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
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
     * Find can invite followingList
     *
     * @param teamId  teamId
     * @param request request
     * @return status code, followingList
     */
    @GetMapping("/followinglist/teamId")
    public ResponseEntity<?> inviteFollowingList(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowingList(findMember);
                            try {
                                Team ft = ts.searchOne(teamId);
                                List<Member> memberList = tms.searchMemberList(ft);
                                List<MemberDTO> dtoList = new ArrayList<>();

                                for (Member following : followingList) {
                                    if (memberList.contains(following)) {
                                        continue;
                                    }
                                    MemberDTO dto = mapping(following);
                                    dtoList.add(dto);
                                }

                                return ResponseEntity.status(HttpStatus.OK).body(dtoList);
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
     * Find followerList
     *
     * @param request request
     * @return status code, followerList
     */
    @GetMapping("/followerlist")
    public ResponseEntity<?> followerList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followerList = fs.searchFollowerList(findMember);
                            List<Member> followingList = fs.searchFollowingList(findMember);
                            List<MemberDTO> followerDtoList = new ArrayList<>();
                            List<MemberDTO> followingDtoList = new ArrayList<>();


                            for (Member follower : followerList) {
                                MemberDTO dto = mapping(follower);
                                followerDtoList.add(dto);
                            }

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                followingDtoList.add(dto);
                            }

                            Follower follower = new Follower();
                            follower.setFollowerList(followerDtoList);
                            follower.setFollowingList(followingDtoList);

                            return ResponseEntity.status(HttpStatus.OK).body(follower);
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
     * Find followerList by name
     *
     * @param name    follower name
     * @param request request
     * @return status code, followerList
     */
    @GetMapping("/followerlist/name")
    public ResponseEntity<?> searchFollowerListByName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followerList = fs.searchFollowerListByName(findMember, name);
                            List<Member> followingList = fs.searchFollowingList(findMember);
                            List<MemberDTO> followerDtoList = new ArrayList<>();
                            List<MemberDTO> followingDtoList = new ArrayList<>();


                            for (Member follower : followerList) {
                                MemberDTO dto = mapping(follower);
                                followerDtoList.add(dto);
                            }

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                followingDtoList.add(dto);
                            }

                            Follower follower = new Follower();
                            follower.setFollowerList(followerDtoList);
                            follower.setFollowingList(followingDtoList);

                            return ResponseEntity.status(HttpStatus.OK).body(follower);
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

    private MemberDTO mapping(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        return dto;
    }
}
