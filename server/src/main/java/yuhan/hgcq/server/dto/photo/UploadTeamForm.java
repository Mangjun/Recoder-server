package yuhan.hgcq.server.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadTeamForm implements Serializable {
    private Long teamId;
    private MultipartFile file;
}
