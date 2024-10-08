package yuhan.hgcq.server.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import yuhan.hgcq.server.config.FileStorageUtil;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.kafka.message.PhotoUploadMessage;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.PhotoService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PhotoUploadConsumer {

    private final PhotoService ps;
    private final MemberService ms;
    private final AlbumService as;

    @Autowired
    public PhotoUploadConsumer(PhotoService ps, MemberService ms, AlbumService as) {
        this.ps = ps;
        this.ms = ms;
        this.as = as;
    }

    @KafkaListener(topics = "photo-upload", groupId = "photo-processing-group")
    public void consumeUploadPhoto(PhotoUploadMessage message) {
        try {
            Member fm = ms.searchOne(message.getMemberId());

            if (fm != null) {
                Album fa = as.searchOne(message.getAlbumId());

                if (fa != null) {
                    List<String> filePaths = message.getFilePaths();
                    List<String> regions = message.getRegions();
                    List<String> creates = message.getCreates();

                    int size = filePaths.size();

                    for (int i = 0; i < size; i++) {
                        try {
                            ps.savePhoto(fa, filePaths.get(i), regions.get(i), creates.get(i));
                            FileStorageUtil.deleteFile(filePaths.get(i));
                        } catch (IOException e) {
                            log.error("Upload Error");
                        }

                    }
                } else {
                    log.error("Album not found");
                }
            } else {
                log.error("Member not found");
            }
        } catch (Exception e) {
            /* 재시도 코드 추가 예정 */
        }
    }
}
