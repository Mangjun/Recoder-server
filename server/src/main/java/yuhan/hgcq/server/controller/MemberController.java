package yuhan.hgcq.server.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.member.*;
import yuhan.hgcq.server.dto.photo.UploadMemberForm;
import yuhan.hgcq.server.service.FollowService;
import yuhan.hgcq.server.service.MemberService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService ms;
    private final FollowService fs;
    private final SessionRepository<? extends Session> sessionRepository;

    /**
     * Join
     *
     * @param form join form
     * @return status code
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignupForm form) {
        try {
            ms.join(form);
            return ResponseEntity.status(HttpStatus.CREATED).body("Join Member Success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Login
     *
     * @param form    login form
     * @param request request
     * @return status code, cookie
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form, HttpServletRequest request) {
        try {
            Member loginMember = ms.login(form);

            if (loginMember != null) {
                MemberDTO memberDTO = mapping(loginMember);

                HttpSession session = request.getSession();
                session.setAttribute("member", memberDTO);

                log.info("Session Create : {}", memberDTO);
                return ResponseEntity.status(HttpStatus.OK).body(memberDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Login Fail");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Logout
     *
     * @param request request
     * @return status code
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();

            log.info("Session Invalidate : {}", session);
            return ResponseEntity.status(HttpStatus.OK).body("Logout Success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout Fail");
        }
    }

    /**
     * Update member information
     *
     * @param form    update member form
     * @param request request
     * @return status code, member dto
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemberUpdateForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            ms.updateMember(findMember, form);
                            MemberDTO memberDTO = mapping(findMember);
                            session.setAttribute("member", memberDTO);
                            return ResponseEntity.status(HttpStatus.OK).body(memberDTO);
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
     * Check duplicate email
     *
     * @param email   email
     * @param request request
     * @return status code, is duplicate?
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity<?> duplicateEmail(@RequestParam("email") String email, HttpServletRequest request) {
        boolean isDuplicateEmail = ms.duplicateEmail(email);

        if (isDuplicateEmail) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    /**
     * Check duplicate name
     *
     * @param name    name
     * @param request request
     * @return status code, is duplicate?
     */
    @GetMapping("/duplicate/name")
    public ResponseEntity<?> duplicateName(@RequestParam("name") String name, HttpServletRequest request) {
        boolean isDuplicateName = ms.duplicateName(name);

        if (isDuplicateName) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    /**
     * Find memberList
     *
     * @param request request
     * @return status code, memberList
     */
    @GetMapping("/list")
    public ResponseEntity<?> memberList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        List<Member> memberList = ms.searchAll();
                        memberList.remove(findMember);
                        List<Member> followingList = fs.searchFollowingList(findMember);

                        List<MemberDTO> memberDtoList = new ArrayList<>();
                        List<MemberDTO> followingDtoList = new ArrayList<>();

                        for (Member member : memberList) {
                            MemberDTO dto = mapping(member);
                            memberDtoList.add(dto);
                        }

                        for (Member following : followingList) {
                            MemberDTO dto = mapping(following);
                            followingDtoList.add(dto);
                        }

                        Members members = new Members();
                        members.setMemberList(memberDtoList);
                        members.setFollowingList(followingDtoList);

                        return ResponseEntity.status(HttpStatus.OK).body(members);
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find memberList by name
     *
     * @param name    member name
     * @param request request
     * @return status code, memberList
     */
    @GetMapping("/list/name")
    public ResponseEntity<?> memberListByName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        List<Member> memberList = ms.searchAllByName(name);
                        memberList.remove(findMember);
                        List<Member> followingList = fs.searchFollowingList(findMember);

                        List<MemberDTO> memberDtoList = new ArrayList<>();
                        List<MemberDTO> followingDtoList = new ArrayList<>();

                        for (Member member : memberList) {
                            MemberDTO dto = mapping(member);
                            memberDtoList.add(dto);
                        }

                        for (Member following : followingList) {
                            MemberDTO dto = mapping(following);
                            followingDtoList.add(dto);
                        }

                        Members members = new Members();
                        members.setMemberList(memberDtoList);
                        members.setFollowingList(followingDtoList);

                        return ResponseEntity.status(HttpStatus.OK).body(members);
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Check login
     *
     * @param request request
     * @return status code, is login?
     */
    @GetMapping("/islogin")
    public ResponseEntity<?> isLogin(HttpServletRequest request) {
        String sessionId = null;
        String decodedString = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSION".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    byte[] decodedBytes = Base64.getDecoder().decode(sessionId);
                    decodedString = new String(decodedBytes);
                    log.debug(sessionId);
                    log.debug(decodedString);
                    break;
                }
            }
        }

        if (sessionId != null) {
            Session session = sessionRepository.findById(decodedString);

            if (session != null) {
                MemberDTO loginMember = (MemberDTO) session.getAttribute("member");
                return ResponseEntity.status(HttpStatus.OK).body(loginMember);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Upload member image
     *
     * @param form    member image form
     * @param request request
     * @return status code
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@ModelAttribute UploadMemberForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            String path = ms.upload(findMember, form);
                            MemberDTO memberDTO = mapping(findMember);
                            session.setAttribute("member", memberDTO);
                            return ResponseEntity.status(HttpStatus.OK).body(path);
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

    private MemberDTO mapping(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setImage(member.getImage());
        return dto;
    }
}
