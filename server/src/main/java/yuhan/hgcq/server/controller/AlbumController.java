package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.album.AlbumCreateForm;
import yuhan.hgcq.server.dto.album.AlbumDTO;
import yuhan.hgcq.server.dto.album.AlbumUpdateForm;
import yuhan.hgcq.server.dto.album.DeleteCancelAlbumForm;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final MemberService ms;
    private final TeamService ts;
    private final AlbumService as;

    /**
     * Create album
     *
     * @param albumCreateForm create album form
     * @param request         request
     * @return status code
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAlbum(@RequestBody AlbumCreateForm albumCreateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.searchOne(albumCreateForm.getTeamId());

                            if (ft != null) {
                                Album newAlbum = new Album(ft, albumCreateForm.getName());

                                try {
                                    as.create(findMember, newAlbum);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Create Album Success");
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
     * Delete album
     *
     * @param albumDTO album dto
     * @param request  request
     * @return status code
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAlbum(@RequestBody AlbumDTO albumDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumDTO.getAlbumId());

                            if (fa != null) {
                                try {
                                    as.deleteAlbum(findMember, fa);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Album Success");
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
     * Delete album cancel
     *
     * @param form    delete album cancel form
     * @param request request
     * @return status code
     */
    @PostMapping("/delete/cancel")
    public ResponseEntity<?> deleteCancelAlbum(@RequestBody DeleteCancelAlbumForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Long> albumIds = form.getAlbumIds();
                            for (Long albumId : albumIds) {
                                Album fa = as.searchOne(albumId);

                                if (fa != null) {
                                    try {
                                        as.deleteAlbumCancel(findMember, fa);
                                    } catch (AccessException e) {
                                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }
                            }
                            return ResponseEntity.status(HttpStatus.OK).body("Delete Cancel Album Success");
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
     * Update album information
     *
     * @param albumUpdateForm album update form
     * @param request         request
     * @return status code
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateAlbum(@RequestBody AlbumUpdateForm albumUpdateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumUpdateForm.getAlbumId());

                            if (fa != null) {
                                String updateName = albumUpdateForm.getName();

                                if (updateName != null) {
                                    fa.changeName(updateName);
                                }

                                try {
                                    as.modify(findMember, fa);
                                    return ResponseEntity.status(HttpStatus.OK).body("Update Album Success");
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
     * Find albumList
     *
     * @param teamId  teamId
     * @param request request
     * @return status code, albumList
     */
    @GetMapping("/list/teamId")
    public ResponseEntity<?> listAlbums(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
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
                                    List<Album> albumList = as.searchAll(ft);

                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : albumList) {
                                        AlbumDTO albumDTO = mapping(album);
                                        albumDTOList.add(albumDTO);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
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
     * Find albumList by name
     *
     * @param teamId  teamId
     * @param name    name
     * @param request request
     * @return status code, albumList
     */
    @GetMapping("/list/teamId/name")
    public ResponseEntity<?> listAlbumsByName(@RequestParam("teamId") Long teamId, @RequestParam("name") String name, HttpServletRequest request) {
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
                                    List<Album> albumList = as.searchByName(ft, name);

                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : albumList) {
                                        AlbumDTO albumDTO = mapping(album);
                                        albumDTOList.add(albumDTO);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
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
     * Find albumTrashList
     *
     * @param teamId  teamId
     * @param request request
     * @return status code, albumTrashList
     */
    @GetMapping("/list/teamId/trash")
    public ResponseEntity<?> listAlbumsTrash(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
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
                                    List<Album> trashList = as.searchAlbumTrashList(ft);

                                    as.trash(trashList);

                                    List<Album> trashListAfterClear = as.searchAlbumTrashList(ft);
                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : trashListAfterClear) {
                                        AlbumDTO dto = mapping(album);
                                        albumDTOList.add(dto);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
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

    private AlbumDTO mapping(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getId());
        dto.setName(album.getName());
        dto.setTeamId(album.getTeam().getId());
        return dto;
    }
}
