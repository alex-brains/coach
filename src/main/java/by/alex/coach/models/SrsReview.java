package by.alex.coach.models;

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

    @NotBlank
    @Column(name = "item_type", nullable = false)
    private String itemType;    // QUESTION | LANGUAGE_ITEM

    @NotBlank
    @Column(name = "item_id", nullable = false)
    private Long itemId;    // id из questions или languages_items

    @NotBlank
    @Column(name = "stage", nullable = false)
    private int stage = 0;

    @NotBlank
    @Column(name = "next_review_at", nullable = false)
    private Instant nextReviewAt;

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    @NotBlank
    @Column(name = "correct_in_row", nullable = false)
    private int correctInRow;

    @NotBlank
    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Instant getNextReviewAt() {
        return nextReviewAt;
    }

    public void setNextReviewAt(Instant nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public Instant getLastReviewedAt() {
        return lastReviewedAt;
    }

    public void setLastReviewedAt(Instant lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    public int getCorrectInRow() {
        return correctInRow;
    }

    public void setCorrectInRow(int correctInRow) {
        this.correctInRow = correctInRow;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // Считаем следующую дату повторения по SRS (простая версия SM-2)
    public void markCorrect() {
        correctInRow++;
        stage++;
        lastReviewedAt = Instant.now();
        nextReviewAt = lastReviewedAt.plusSeconds((long) Math.pow(2, stage) * 3600); // упрощённо
    }

    public void markIncorrect() {
        correctInRow = 0;
        stage = 0;
        lastReviewedAt = Instant.now();
        nextReviewAt = lastReviewedAt.plusSeconds(3600); // через 1 час
    }
}
