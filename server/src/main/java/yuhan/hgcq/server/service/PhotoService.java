package yuhan.hgcq.server.service;

import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.photo.AutoSavePhotoForm;
import yuhan.hgcq.server.dto.photo.UploadPhotoForm;

import java.io.IOException;
import java.util.List;

public interface PhotoService {

    Long savePhoto(Photo photo) throws IllegalArgumentException;
    void savePhoto(Album album, String path, String region, String create) throws IOException;
    void savePhoto(UploadPhotoForm form) throws IOException, IllegalArgumentException;
    void deletePhoto(Photo photo) throws IllegalArgumentException;
    void deleteCancelPhoto(Photo photo) throws IllegalArgumentException;
    void trash(List<Photo> photos);
    Photo searchOne(Long id) throws IllegalArgumentException;
    Photo searchOne(String path) throws IllegalArgumentException;
    List<Photo> searchAll(Album album) throws IllegalArgumentException;
    List<Photo> searchTrashList(Album album) throws IllegalArgumentException;
    void autoSave(AutoSavePhotoForm form) throws IOException;
    void autoSave(Team team, String path, String region, String create) throws IOException;
    void move(Album newAlbum, List<Photo> photos) throws IOException, IllegalArgumentException;
}
