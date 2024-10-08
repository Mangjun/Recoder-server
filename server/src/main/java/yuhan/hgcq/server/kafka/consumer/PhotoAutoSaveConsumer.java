package yuhan.hgcq.server.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import yuhan.hgcq.server.config.FileStorageUtil;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.kafka.message.PhotoAutoSaveMessage;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.PhotoService;
import yuhan.hgcq.server.service.TeamService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PhotoAutoSaveConsumer {

    private final PhotoService ps;
    private final MemberService ms;
    private final TeamService ts;

    @Autowired
    public PhotoAutoSaveConsumer(PhotoService ps, MemberService ms, TeamService ts) {
        this.ps = ps;
        this.ms = ms;
        this.ts = ts;
    }

    @KafkaListener(topics = "photo-auto-save", groupId = "photo-processing-group")
    public void consumeAutoSavePhoto(PhotoAutoSaveMessage message) {
        try {
            Member fm = ms.searchOne(message.getMemberId());

            if (fm != null) {
                Team ft = ts.searchOne(message.getTeamId());

                if (ft != null) {
                    List<String> filePaths = message.getFilePaths();
                    List<String> regions = message.getRegions();
                    List<String> creates = message.getCreates();

                    int size = filePaths.size();

                    for (int i = 0; i < size; i++) {
                        try {
                            ps.autoSave(ft, filePaths.get(i), regions.get(i), creates.get(i));
                            FileStorageUtil.deleteFile(filePaths.get(i));
                        } catch (IOException e) {
                            log.error("Auto Save Error : {}", e.getMessage());
                        }
                    }
                } else {
                    log.error("Team not found");
                }
            } else {
                log.error("Member not found");
            }
        } catch (Exception e) {
            /* 재시도 코드 추가 예정 */
        }
    }
}
