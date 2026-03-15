package by.alex.coach.service;

import by.alex.coach.models.SrsReview;
import by.alex.coach.repository.SrsReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class SrsReviewSelectionService {
    private final SrsReviewRepository reviewRepository;

    @Autowired
    public SrsReviewSelectionService(SrsReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Optional<SrsReview> nextDue() {
        return reviewRepository.findDueReviews(Instant.now(), PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }
}
