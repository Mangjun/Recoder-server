package yuhan.hgcq.server.dto.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCancelAlbumForm implements Serializable {
    private List<Long> albumIds;
}
