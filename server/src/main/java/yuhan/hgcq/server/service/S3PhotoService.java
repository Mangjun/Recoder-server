package yuhan.hgcq.server.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.photo.AutoSavePhotoForm;
import yuhan.hgcq.server.dto.photo.UploadPhotoForm;
import yuhan.hgcq.server.repository.AlbumRepository;
import yuhan.hgcq.server.repository.LikedRepository;
import yuhan.hgcq.server.repository.PhotoRepository;
import yuhan.hgcq.server.repository.TeamRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class S3PhotoService implements PhotoService {
    private static final Logger log = LoggerFactory.getLogger(LocalPhotoService.class);

    private final PhotoRepository pr;
    private final AlbumRepository ar;
    private final TeamRepository tr;
    private final LikedRepository lr;
    private final S3Operations s3Operations;

    private final static int DELETE_DAY = 30;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * Upload photo
     *
     * @param photo photo
     * @return photoId
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    @Transactional
    public Long savePhoto(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        Long saveId = pr.save(photo);
        log.info("Save Photo : {}", photo);
        return saveId;
    }

    @Override
    @Transactional
    public void savePhoto(Album album, String path, String region, String create) throws IOException {

    }

    /**
     * Upload photoList
     *
     * @param form photoList
     * @throws IOException              Upload error
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    @Transactional
    public void savePhoto(UploadPhotoForm form) throws IOException, IllegalArgumentException {
        Long albumId = form.getAlbumId();
        List<MultipartFile> files = form.getFiles();
        List<String> regions = form.getRegions();
        List<String> creates = form.getCreates();

        ensureNotNull(files, "Files");
        ensureNotNull(creates, "Creates");

        int size = files.size();
        Album fa = ar.findOne(albumId);
        List<String> nameList = pr.findNameAll(fa);

        for (int i = 0; i < size; i++) {
            MultipartFile file = files.get(i);
            String name = file.getOriginalFilename();

            if (nameList.contains(name)) {
                continue;
            }

            String key = "images/" + albumId + "/" + name;

            try (InputStream inputStream = file.getInputStream()) {
                s3Operations.upload(bucketName, key, inputStream,
                        ObjectMetadata.builder().contentType(file.getContentType()).build());
                Photo photo = new Photo(fa, name, key, regions.get(i), LocalDateTime.parse(creates.get(i)));
                pr.save(photo);
                log.info("Save Photo : {}", photo);
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    /**
     * Delete photo
     *
     * @param photo photo
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    @Transactional
    public void deletePhoto(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        photo.delete();

        pr.save(photo);
        lr.delete(photo);
        log.info("Delete Photo : {}", photo);
    }

    /**
     * Delete photo cancel
     *
     * @param photo photo
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    @Transactional
    public void deleteCancelPhoto(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        photo.cancelDelete();

        pr.save(photo);
        log.info("Delete Cancel Photo : {}", photo);
    }

    /**
     * Trash empty
     *
     * @param photos photoTrashList
     */
    @Override
    @Transactional
    public void trash(List<Photo> photos) {
        LocalDateTime now = LocalDateTime.now();
        for (Photo photo : photos) {
            LocalDateTime deleted = photo.getDeleted();
            long between = ChronoUnit.DAYS.between(deleted, now);

            if (between >= DELETE_DAY) {
                String key = photo.getPath();
                s3Operations.deleteObject(bucketName, key);
                pr.delete(photo.getId());
                log.info("Complete Delete Photo : {}", photo);
            }
        }
    }

    /**
     * Find photo by path
     *
     * @param id photoId
     * @return photo
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    public Photo searchOne(Long id) throws IllegalArgumentException {
        Photo find = pr.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("Photo not found");
        }

        return find;
    }

    /**
     * Find photo by path
     *
     * @param path path
     * @return photo
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    public Photo searchOne(String path) throws IllegalArgumentException {
        Photo find = pr.findByPath(path);

        if (find == null) {
            throw new IllegalArgumentException("Photo not found");
        }

        return find;
    }

    /**
     * Find photoList
     *
     * @param album album
     * @return photoList
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    public List<Photo> searchAll(Album album) throws IllegalArgumentException {
        ensureNotNull(album, "Album");

        return pr.findAll(album);
    }

    /**
     * Find photoTrashList
     *
     * @param album album
     * @return photoTrashList
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    public List<Photo> searchTrashList(Album album) throws IllegalArgumentException {
        ensureNotNull(album, "Album");

        return pr.findByDeleted(album);
    }

    /**
     * Auto save photoList
     *
     * @param form photoList
     * @throws IOException Upload error
     */
    @Override
    @Transactional
    public void autoSave(AutoSavePhotoForm form) throws IOException {
        Long teamId = form.getTeamId();
        Team ft = tr.findOne(teamId);

        Set<String> albumNames = ar.findAlbumName(ft);
        List<MultipartFile> files = form.getFiles();
        List<String> creates = form.getCreates();
        List<String> regions = form.getRegions();

        int size = files.size();

        for (int i = 0; i < size; i++) {
            String region = regions.get(i);
            Album fa = null;

            if (region.equals("null")) {
                if (albumNames.contains("위치정보없음")) {
                    fa = ar.findOneByName(ft, "위치정보없음");
                } else {
                    Album album = new Album(ft, "위치정보없음");
                    Long saveId = ar.save(album);
                    log.info("Save Album : {}", album);
                    fa = ar.findOne(saveId);
                    albumNames.add(fa.getName());
                }
            } else if (albumNames.contains(region)) {
                fa = ar.findOneByName(ft, region);
            } else {
                Album album = new Album(ft, region);
                Long saveId = ar.save(album);
                log.info("Save Album : {}", album);
                fa = ar.findOne(saveId);
                albumNames.add(fa.getName());
            }

            if (fa != null) {
                List<String> nameList = pr.findNameAll(fa);
                Long albumId = fa.getId();

                MultipartFile file = files.get(i);
                String name = file.getOriginalFilename();

                if (nameList.contains(name)) {
                    continue;
                }

                String key = "images/" + albumId + "/" + name;

                try (InputStream inputStream = file.getInputStream()) {
                    s3Operations.upload(bucketName, key, inputStream,
                            ObjectMetadata.builder().contentType(file.getContentType()).build());
                    Photo photo = new Photo(fa, name, key, regions.get(i), LocalDateTime.parse(creates.get(i)));
                    pr.save(photo);
                    log.info("AutoSave Photo : {}", photo);
                } catch (IOException e) {
                    log.error("AutoSave Photo Error");
                    throw new IOException(e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void autoSave(Team team, String path, String region, String create) throws IOException {

    }

    /**
     * Move photo to album
     *
     * @param newAlbum new album
     * @param photos   photoList
     * @throws IllegalArgumentException Argument is wrong
     */
    @Override
    @Transactional
    public void move(Album newAlbum, List<Photo> photos) throws IOException, IllegalArgumentException {
        ensureNotNull(newAlbum, "Album");
        ensureNotNull(photos, "Photos");

        for (Photo photo : photos) {
            String oldPath = photo.getPath();
            S3Resource download = s3Operations.download(bucketName, photo.getPath());

            try (InputStream inputStream = download.getInputStream()) {
                String newPath = "images/" + newAlbum.getId() + "/" + photo.getName();
                s3Operations.upload(bucketName, newPath, inputStream);
                s3Operations.deleteObject(bucketName, oldPath);
                photo.changeAlbum(newAlbum, newPath);
                pr.save(photo);
                log.info("Move Photo : {}", photo);
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
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
}
