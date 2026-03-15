package by.alex.coach.service;

import by.alex.coach.models.SrsReview;
import by.alex.coach.repository.SrsReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class SrsReviewService {
    private final SrsReviewRepository reviewRepository;

    @Autowired
    public SrsReviewService(SrsReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public SrsReview getOrCreate(String itemType, Long itemId) {
        return reviewRepository.findByItemTypeAndItemId(itemType, itemId)
                .orElseGet(() -> {
                    SrsReview review = new SrsReview();
                    review.setItemType(itemType);
                    review.setItemId(itemId);
                    review.setNextReviewAt(Instant.now());

                    return reviewRepository.save(review);
                });
    }

    public void markCorrect(SrsReview review) {
        review.markCorrect();
    }

    public void markIncorrect(SrsReview review) {
        review.markIncorrect();
    }
}
