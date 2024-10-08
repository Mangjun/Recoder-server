package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @Id @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    private String message;
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (album == null) {
            throw new IllegalStateException("Album is null");
        }
    }

    public Chat(Member writer, String message, Album album) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        } else if (album == null) {
            throw new IllegalArgumentException("Album cannot be null");
        }
        this.writer = writer;
        this.message = message;
        this.album = album;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", album=" + album +
                ", writer=" + writer +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
