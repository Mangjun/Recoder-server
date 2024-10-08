package yuhan.hgcq.server.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import yuhan.hgcq.server.kafka.message.PhotoUploadMessage;

@Service
public class PhotoUploadProducer {

    private static final String TOPIC = "photo-upload";

    private final KafkaTemplate<String, PhotoUploadMessage> kafkaTemplate;

    @Autowired
    public PhotoUploadProducer(KafkaTemplate<String, PhotoUploadMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUploadPhotoMessage(PhotoUploadMessage message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
