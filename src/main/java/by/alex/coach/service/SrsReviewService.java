package by.alex.coach.service;

import by.alex.coach.models.SrsReview;
import by.alex.coach.models.enums.SrsStage;
import by.alex.coach.repository.SrsReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class SrsReviewService {

    private final SrsReviewRepository reviewRepository;

    public SrsReviewService(SrsReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // ─── Выборка ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<SrsReview> nextDue(String itemType, Long topicId) {
        Instant now = Instant.now();

        if (topicId != null) {
            if ("QUESTION".equals(itemType)) {
                return reviewRepository.findDueQuestionByTopic(now, topicId);
            }
            if ("LANGUAGE_ITEM".equals(itemType)) {
                return reviewRepository.findDueLanguageItemByTopic(now, topicId);
            }
        }

        if (itemType != null) {
            return reviewRepository
                    .findDueReviewsByType(now, itemType, PageRequest.of(0, 1))
                    .stream().findFirst();
        }

        return reviewRepository
                .findDueReviews(now, PageRequest.of(0, 1))
                .stream().findFirst();
    }

    /**
     * Счётчик карточек с учётом всех фильтров.
     * itemType = null, topicId = null → все карточки
     * itemType = QUESTION, topicId = null → все вопросы
     * itemType = QUESTION, topicId = 5 → вопросы в топике 5
     */
    @Transactional(readOnly = true)
    public long countDue(String itemType, Long topicId) {
        Instant now = Instant.now();

        if (topicId != null) {
            if ("QUESTION".equals(itemType)) {
                return reviewRepository.countDueQuestionsByTopic(now, topicId);
            }
            if ("LANGUAGE_ITEM".equals(itemType)) {
                return reviewRepository.countDueLanguageItemsByTopic(now, topicId);
            }
        }

        if (itemType != null) {
            return reviewRepository.countDueReviewsByType(now, itemType);
        }

        return reviewRepository.countDueReviews(now);
    }

    // Обратная совместимость для StudyController (Thymeleaf)
    @Transactional(readOnly = true)
    public long countDue(String itemType) {
        return countDue(itemType, null);
    }

    @Transactional(readOnly = true)
    public SrsReview findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SrsReview not found: " + id));
    }

    // ─── Создание ───────────────────────────────────────────────────────────

    public SrsReview getOrCreate(String itemType, Long itemId) {
        return reviewRepository
                .findByItemTypeAndItemId(itemType, itemId)
                .orElseGet(() -> {
                    SrsReview review = new SrsReview();
                    review.setItemType(itemType);
                    review.setItemId(itemId);
                    review.setStage(SrsStage.NEW);
                    review.setNextReviewAt(Instant.now());
                    return reviewRepository.save(review);
                });
    }

    // ─── SRS логика ─────────────────────────────────────────────────────────

    public void markCorrect(SrsReview review) {
        SrsStage nextStage = review.getStage().next();
        review.setStage(nextStage);
        review.setCorrectInRow(review.getCorrectInRow() + 1);
        review.setLastReviewedAt(Instant.now());

        if (nextStage.isArchived()) {
            review.setArchived(true);
            review.setNextReviewAt(Instant.now());
        } else {
            review.setNextReviewAt(Instant.now().plus(nextStage.getInterval()));
        }
        reviewRepository.save(review);
    }

    public void markIncorrect(SrsReview review) {
        review.setStage(SrsStage.NEW);
        review.setCorrectInRow(0);
        review.setLastReviewedAt(Instant.now());
        review.setNextReviewAt(Instant.now());
        reviewRepository.save(review);
    }
}
