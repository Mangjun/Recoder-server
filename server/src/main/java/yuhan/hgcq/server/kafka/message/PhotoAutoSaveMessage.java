package yuhan.hgcq.server.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAutoSaveMessage {
    private Long teamId;
    private Long memberId;
    private List<String> filePaths;
    private List<String> creates;
    private List<String> regions;
}
