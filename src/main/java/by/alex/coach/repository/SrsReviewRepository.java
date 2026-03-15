package by.alex.coach.repository;

import by.alex.coach.models.SrsReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SrsReviewRepository extends JpaRepository<SrsReview, Long> {
    Optional<SrsReview> findByItemTypeAndItemId(String itemType, Long itemId);

    @Query("""
        select r from SrsReview r
        where r.archived = false and r.nextReviewAt <= :now
        order by r.nextReviewAt
        """)
    List<SrsReview> findDueReviews(Instant now, Pageable pageable);
}
