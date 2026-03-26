package by.alex.coach.repository;

import by.alex.coach.models.SrsReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SrsReviewRepository extends JpaRepository<SrsReview, Long> {

    Optional<SrsReview> findByItemTypeAndItemId(String itemType, Long itemId);

    // ─── Выборка следующей карточки ──────────────────────────────────────────

    @Query("""
        select r from SrsReview r
        where r.archived = false
          and r.nextReviewAt <= :now
        order by r.nextReviewAt asc
        """)
    List<SrsReview> findDueReviews(@Param("now") Instant now, Pageable pageable);

    @Query("""
        select r from SrsReview r
        where r.archived = false
          and r.nextReviewAt <= :now
          and r.itemType = :itemType
        order by r.nextReviewAt asc
        """)
    List<SrsReview> findDueReviewsByType(
            @Param("now") Instant now,
            @Param("itemType") String itemType,
            Pageable pageable
    );

    @Query(value = """
        select r.* from srs_reviews r
        join questions q on r.item_id = q.id
        where r.item_type = 'QUESTION'
          and r.archived = false
          and r.next_review_at <= :now
          and q.topic_id = :topicId
        order by r.next_review_at asc
        limit 1
        """, nativeQuery = true)
    Optional<SrsReview> findDueQuestionByTopic(
            @Param("now") Instant now,
            @Param("topicId") Long topicId
    );

    @Query(value = """
        select r.* from srs_reviews r
        join languages_items l on r.item_id = l.id
        where r.item_type = 'LANGUAGE_ITEM'
          and r.archived = false
          and r.next_review_at <= :now
          and l.topic_id = :topicId
        order by r.next_review_at asc
        limit 1
        """, nativeQuery = true)
    Optional<SrsReview> findDueLanguageItemByTopic(
            @Param("now") Instant now,
            @Param("topicId") Long topicId
    );

    // ─── Счётчики ────────────────────────────────────────────────────────────

    @Query("""
        select count(r) from SrsReview r
        where r.archived = false
          and r.nextReviewAt <= :now
        """)
    long countDueReviews(@Param("now") Instant now);

    @Query("""
        select count(r) from SrsReview r
        where r.archived = false
          and r.nextReviewAt <= :now
          and r.itemType = :itemType
        """)
    long countDueReviewsByType(
            @Param("now") Instant now,
            @Param("itemType") String itemType
    );

    @Query(value = """
        select count(*) from srs_reviews r
        join questions q on r.item_id = q.id
        where r.item_type = 'QUESTION'
          and r.archived = false
          and r.next_review_at <= :now
          and q.topic_id = :topicId
        """, nativeQuery = true)
    long countDueQuestionsByTopic(
            @Param("now") Instant now,
            @Param("topicId") Long topicId
    );

    @Query(value = """
        select count(*) from srs_reviews r
        join languages_items l on r.item_id = l.id
        where r.item_type = 'LANGUAGE_ITEM'
          and r.archived = false
          and r.next_review_at <= :now
          and l.topic_id = :topicId
        """, nativeQuery = true)
    long countDueLanguageItemsByTopic(
            @Param("now") Instant now,
            @Param("topicId") Long topicId
    );

    @Query("""
        select r.stage, count(r) from SrsReview r
        where r.archived = false
        group by r.stage
        """)
    List<Object[]> countByStage();
}
