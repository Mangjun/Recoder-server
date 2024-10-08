package yuhan.hgcq.server.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import yuhan.hgcq.server.kafka.message.PhotoAutoSaveMessage;

@Service
public class PhotoAutoSaveProducer {

    private static final String TOPIC = "photo-auto-save";

    private final KafkaTemplate<String, PhotoAutoSaveMessage> kafkaTemplate;

    @Autowired
    public PhotoAutoSaveProducer(KafkaTemplate<String, PhotoAutoSaveMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAutoSavePhotoMessage(PhotoAutoSaveMessage message) {
       kafkaTemplate.send(TOPIC, message);
    }
}
