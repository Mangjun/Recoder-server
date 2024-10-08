package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.*;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.dto.photo.LikedDTO;
import yuhan.hgcq.server.dto.photo.PhotoDTO;
import yuhan.hgcq.server.service.LikedService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.PhotoService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/liked")
public class LikedController {

    private final MemberService ms;
    private final PhotoService ps;
    private final LikedService ls;

    /**
     * Create like
     *
     * @param likedDTO like dto
     * @param request  request
     * @return status code
     */
    @PostMapping("/add")
    public ResponseEntity<?> createLiked(@RequestBody LikedDTO likedDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Photo fp = ps.searchOne(likedDTO.getPhotoId());

                            if (fp != null) {
                                Liked newLiked = new Liked(findMember, fp);
                                try {
                                    ls.addLike(newLiked);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Add Liked Success");
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
     * Delete like
     *
     * @param likedDTO like dto
     * @param request  request
     * @return status code
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteLiked(@RequestBody LikedDTO likedDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Photo fp = ps.searchOne(likedDTO.getPhotoId());

                            if (fp != null) {
                                try {
                                    Liked fl = ls.searchOne(findMember, fp);

                                    if (fl != null) {
                                        try {
                                            ls.removeLike(fl);
                                            return ResponseEntity.status(HttpStatus.OK).body("Delete Liked Success");
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
     * Find likeList
     *
     * @param request request
     * @return status code, likeList
     */
    @GetMapping("/list")
    public ResponseEntity<?> listLiked(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Photo> likedList = ls.searchAll(findMember);
                            List<PhotoDTO> photoDTOList = new ArrayList<>();

                            for (Photo photo : likedList) {
                                PhotoDTO dto = mapping(photo);
                                dto.setIsLiked(true);
                                photoDTOList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(photoDTOList);
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

    private PhotoDTO mapping(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setPhotoId(photo.getId());
        dto.setAlbumId(photo.getAlbum().getId());
        dto.setName(photo.getName());
        dto.setPath(photo.getPath());
        dto.setCreated(photo.getCreated().toString());
        return dto;
    }
}
