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

    /**
     * Следующая карточка с учётом фильтров.
     *
     * @param itemType  null = все, "QUESTION" или "LANGUAGE_ITEM"
     * @param topicId   null = все топики
     */
    @Transactional(readOnly = true)
    public Optional<SrsReview> nextDue(String itemType, Long topicId) {
        Instant now = Instant.now();

        // Фильтр по топику — используем нативные запросы
        if (topicId != null) {
            if ("QUESTION".equals(itemType)) {
                return reviewRepository.findDueQuestionByTopic(now, topicId);
            }
            if ("LANGUAGE_ITEM".equals(itemType)) {
                return reviewRepository.findDueLanguageItemByTopic(now, topicId);
            }
        }

        // Фильтр только по типу
        if (itemType != null) {
            return reviewRepository
                    .findDueReviewsByType(now, itemType, PageRequest.of(0, 1))
                    .stream().findFirst();
        }

        // Без фильтров
        return reviewRepository
                .findDueReviews(now, PageRequest.of(0, 1))
                .stream().findFirst();
    }

    @Transactional(readOnly = true)
    public long countDue(String itemType) {
        if (itemType != null) {
            return reviewRepository.countDueReviewsByType(Instant.now(), itemType);
        }
        return reviewRepository.countDueReviews(Instant.now());
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
