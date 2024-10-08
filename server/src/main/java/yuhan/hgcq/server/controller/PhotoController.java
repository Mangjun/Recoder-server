package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.dto.photo.*;
import yuhan.hgcq.server.kafka.producer.PhotoAutoSaveProducer;
import yuhan.hgcq.server.kafka.producer.PhotoUploadProducer;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.LikedService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.PhotoService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photo")
public class PhotoController {

    private final MemberService ms;
    private final AlbumService as;
    private final PhotoService ps;
    private final LikedService ls;
    private final PhotoUploadProducer up;
    private final PhotoAutoSaveProducer asp;

    /**
     * Upload photo
     *
     * @param form    upload form
     * @param request request
     * @return status code
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhotos(@ModelAttribute UploadPhotoForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(form.getAlbumId());

                            if (fa != null) {
                                try {
//                                    List<MultipartFile> files = form.getFiles();
//                                    List<String> paths = new ArrayList<>();
//
//                                    for (MultipartFile file : files) {
//                                        try {
//                                            String tempPath = FileStorageUtil.saveFile(file);
//                                            paths.add(tempPath);
//                                        } catch (IOException e) {
//                                            for (String path : paths) {
//                                                FileStorageUtil.deleteFile(path);
//                                            }
//                                        }
//                                    }
//
//                                    PhotoUploadMessage message = new PhotoUploadMessage(
//                                            fa.getId(),
//                                            findMember.getId(),
//                                            paths,
//                                            form.getCreates(),
//                                            form.getRegions()
//                                    );
//
//                                    up.sendUploadPhotoMessage(message);
                                    ps.savePhoto(form);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Upload Photo Success");
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
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Delete photo
     *
     * @param photoDTO photo dto
     * @param request  request
     * @return status code
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePhoto(@RequestBody PhotoDTO photoDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Photo fp = ps.searchOne(photoDTO.getPhotoId());

                            if (fp != null) {
                                try {
                                    ps.deletePhoto(fp);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Photo Success");
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
     * Delete photo cancel
     *
     * @param form    delete photo cancel form
     * @param request request
     * @return status code
     */
    @PostMapping("/delete/cancel")
    public ResponseEntity<?> cancelDeletePhoto(@RequestBody DeleteCancelPhotoForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Long> photoIds = form.getPhotoIds();

                            for (Long photoId : photoIds) {
                                Photo fp = ps.searchOne(photoId);

                                if (fp != null) {
                                    try {
                                        ps.deleteCancelPhoto(fp);
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
                                    }
                                }
                            }
                            return ResponseEntity.status(HttpStatus.OK).body("Delete Cancel Photo Success");
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
     * Move photo
     *
     * @param form    move form
     * @param request request
     * @return status code
     */
    @PostMapping("/move")
    public ResponseEntity<?> movePhoto(@RequestBody MovePhotoForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(form.getNewAlbumId());

                            if (fa != null) {
                                List<PhotoDTO> photos = form.getPhotos();
                                List<Photo> photoList = new ArrayList<>();

                                for (PhotoDTO photoDTO : photos) {
                                    try {
                                        Photo fp = ps.searchOne(photoDTO.getPhotoId());
                                        photoList.add(fp);
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                    }
                                }

                                try {
                                    ps.move(fa, photoList);
                                    return ResponseEntity.status(HttpStatus.OK).body("Move Photo Success");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                                } catch (IOException e) {
                                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
     * Auto save photoList
     *
     * @param form    photoList form
     * @param request request
     * @return status code
     */
    @PostMapping("/autosave")
    public ResponseEntity<?> autosavePhoto(@ModelAttribute AutoSavePhotoForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
//                            List<MultipartFile> files = form.getFiles();
//                            List<String> paths = new ArrayList<>();
//
//                            for (MultipartFile file : files) {
//                                try {
//                                    String tempPath = FileStorageUtil.saveFile(file);
//                                    paths.add(tempPath);
//                                } catch (IOException e) {
//                                    for (String path : paths) {
//                                        FileStorageUtil.deleteFile(path);
//                                    }
//                                }
//                            }
//
//                            PhotoAutoSaveMessage message = new PhotoAutoSaveMessage(
//                                    form.getTeamId(),
//                                    findMember.getId(),
//                                    paths,
//                                    form.getCreates(),
//                                    form.getRegions()
//                            );
//
//                            asp.sendAutoSavePhotoMessage(message);
                            ps.autoSave(form);
                            return ResponseEntity.status(HttpStatus.OK).body("Autosave Photo Success");
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

    /**
     * Find gallery
     *
     * @param albumId albumId
     * @param request request
     * @return status code, gallery
     */
    @GetMapping("/gallery/albumId")
    public ResponseEntity<?> gallery(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumId);

                            if (fa != null) {
                                try {
                                    List<Photo> photoList = ps.searchAll(fa);
                                    List<Photo> likeList = ls.searchAll(findMember);
                                    Map<String, List<PhotoDTO>> gallery = new HashMap<>();

                                    for (Photo photo : photoList) {
                                        LocalDate create = photo.getCreated().toLocalDate();
                                        PhotoDTO dto = mapping(photo);
                                        dto.setIsLiked(likeList.contains(photo));

                                        List<PhotoDTO> photoDTOList = gallery.getOrDefault(create.toString(), new ArrayList<>());

                                        photoDTOList.add(dto);

                                        gallery.put(create.toString(), photoDTOList);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(gallery);
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
     * Find photoList
     *
     * @param albumId albumId
     * @param request request
     * @return status code, photoList
     */
    @GetMapping("/list/albumId")
    public ResponseEntity<?> listPhoto(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumId);

                            if (fa != null) {
                                try {
                                    List<Photo> photoList = ps.searchAll(fa);
                                    List<Photo> likeList = ls.searchAll(findMember);
                                    List<PhotoDTO> photoDTOList = new ArrayList<>();

                                    for (Photo photo : photoList) {
                                        PhotoDTO dto = mapping(photo);
                                        dto.setIsLiked(likeList.contains(photo));
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
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Find photoTrashList
     *
     * @param albumId albumId
     * @param request request
     * @return status code, photoTrashList
     */
    @GetMapping("/list/albumId/trash")
    public ResponseEntity<?> listPhotoTrash(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.searchOne(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.searchOne(albumId);

                            if (fa != null) {
                                try {
                                    List<Photo> trashList = ps.searchTrashList(fa);
                                    ps.trash(trashList);

                                    List<Photo> trashListAfterClear = ps.searchTrashList(fa);
                                    List<PhotoDTO> photoDTOList = new ArrayList<>();

                                    for (Photo photo : trashListAfterClear) {
                                        PhotoDTO dto = mapping(photo);
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
        dto.setCreated(photo.getCreated().toString());
        dto.setRegion(photo.getRegion());
        dto.setName(photo.getName());
        dto.setPath(photo.getPath());
        return dto;
    }
}
