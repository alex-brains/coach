package by.alex.coach.models;

import by.alex.coach.models.enums.SrsStage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "srs_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uq_review", columnNames = {"item_type", "item_id"})
})
public class SrsReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_type", nullable = false)
    private String itemType;    // QUESTION | LANGUAGE_ITEM

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    /**
     * Текущая стадия SRS. Хранится как строка (имя enum).
     * Используем @Enumerated(STRING) — читаемо в БД, не сломается при добавлении новых стадий.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private SrsStage stage = SrsStage.NEW;

    @Column(name = "next_review_at", nullable = false)
    private Instant nextReviewAt = Instant.now();   // NEW → доступна сразу

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    @Column(name = "correct_in_row", nullable = false)
    private int correctInRow = 0;

    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public SrsStage getStage() { return stage; }
    public void setStage(SrsStage stage) { this.stage = stage; }

    public Instant getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(Instant nextReviewAt) { this.nextReviewAt = nextReviewAt; }

    public Instant getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(Instant lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }

    public int getCorrectInRow() { return correctInRow; }
    public void setCorrectInRow(int correctInRow) { this.correctInRow = correctInRow; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
