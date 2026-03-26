package by.alex.coach.media;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "media_files", indexes = {
        @Index(name = "idx_media_files_item", columnList = "item_type, item_id")
})
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;          // uuid.ext — реальное имя на диске

    @Column(name = "original_name", nullable = false)
    private String originalName;      // оригинальное имя от пользователя

    @Column(name = "content_type", nullable = false)
    private String contentType;       // image/png, image/jpeg...

    @Column(name = "size", nullable = false)
    private long size;                // в байтах

    @Column(name = "item_type")
    private String itemType;          // QUESTION | LANGUAGE_ITEM | null

    @Column(name = "item_id")
    private Long itemId;              // к чему прикреплён файл

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
