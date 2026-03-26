package by.alex.coach.controllers.api;

import by.alex.coach.models.SrsReview;
import by.alex.coach.models.enums.SrsStage;
import by.alex.coach.media.MediaService;
import by.alex.coach.service.LanguagesItemService;
import by.alex.coach.service.QuestionService;
import by.alex.coach.service.SrsReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/study")
public class StudyApiController {

    private final SrsReviewService reviewService;
    private final QuestionService questionService;
    private final LanguagesItemService languagesItemService;
    private final MediaService mediaService;

    public StudyApiController(SrsReviewService reviewService,
                              QuestionService questionService,
                              LanguagesItemService languagesItemService,
                              MediaService mediaService) {
        this.reviewService = reviewService;
        this.questionService = questionService;
        this.languagesItemService = languagesItemService;
        this.mediaService = mediaService;
    }

    @GetMapping("/next")
    public ResponseEntity<Map<String, Object>> next(
            @RequestParam(required = false) String itemType,
            @RequestParam(required = false) Long topicId
    ) {
        SrsReview review = reviewService.nextDue(itemType, topicId).orElse(null);

        // Счётчик учитывает оба фильтра
        long dueCount = reviewService.countDue(itemType, topicId);

        if (review == null) {
            return ResponseEntity.ok(Map.of("dueCount", dueCount));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reviewId", review.getId());
        response.put("itemType", review.getItemType());
        response.put("stage", review.getStage().name());
        response.put("dueCount", dueCount);

        if ("QUESTION".equals(review.getItemType())) {
            var q = questionService.getQuestionById(review.getItemId());
            response.put("question", Map.of(
                    "id", q.id(),
                    "questionText", q.question(),
                    "answerText", q.answer(),
                    "topicName", q.topicName() != null ? q.topicName() : ""
            ));
        } else {
            var item = languagesItemService.getItemsById(review.getItemId());
            response.put("item", Map.of(
                    "id", item.id(),
                    "word", item.word(),
                    "translation", item.translation(),
                    "example", item.example() != null ? item.example() : "",
                    "language", item.language(),
                    "type", item.type()
            ));
        }

        List<Map<String, Object>> media = mediaService
                .getByItem(review.getItemType(), review.getItemId())
                .stream()
                .map(f -> Map.<String, Object>of(
                        "id", f.getId(),
                        "url", "/api/media/" + f.getId(),
                        "filename", f.getOriginalName()
                ))
                .toList();
        response.put("media", media);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reviewId}/correct")
    public ResponseEntity<Map<String, Object>> correct(@PathVariable Long reviewId) {
        SrsReview review = reviewService.findById(reviewId);
        SrsStage stageBefore = review.getStage();
        reviewService.markCorrect(review);
        return ResponseEntity.ok(Map.of(
                "stageBefore", stageBefore.name(),
                "stageAfter", review.getStage().name(),
                "archived", review.isArchived(),
                "nextReviewAt", review.getNextReviewAt().toString()
        ));
    }

    @PostMapping("/{reviewId}/incorrect")
    public ResponseEntity<Map<String, Object>> incorrect(@PathVariable Long reviewId) {
        SrsReview review = reviewService.findById(reviewId);
        reviewService.markIncorrect(review);
        return ResponseEntity.ok(Map.of(
                "stageAfter", review.getStage().name(),
                "nextReviewAt", review.getNextReviewAt().toString()
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "dueAll", reviewService.countDue(null, null),
                "dueQuestions", reviewService.countDue("QUESTION", null),
                "dueLanguage", reviewService.countDue("LANGUAGE_ITEM", null)
        ));
    }
}
