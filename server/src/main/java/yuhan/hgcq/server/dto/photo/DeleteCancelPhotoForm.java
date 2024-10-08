package yuhan.hgcq.server.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCancelPhotoForm implements Serializable {
    private List<Long> photoIds;
}
