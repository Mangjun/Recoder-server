package yuhan.hgcq.server.dto.photo;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadPhotoForm implements Serializable {
    private Long albumId;
    private List<MultipartFile> files;
    private List<String> creates;
    private List<String> regions;
}
