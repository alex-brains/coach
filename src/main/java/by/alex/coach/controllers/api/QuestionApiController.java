package by.alex.coach.controllers.api;

import by.alex.coach.dto.question.QuestionForm;
import by.alex.coach.dto.question.QuestionListDto;
import by.alex.coach.dto.question.QuestionViewDto;
import by.alex.coach.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionApiController {
    private final QuestionService questionService;

    public QuestionApiController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * GET /api/v1/questions
     * GET /api/v1/questions?topicId=1
     */
    @GetMapping
    public ResponseEntity<List<QuestionListDto>> getAll(
            @RequestParam(required = false) Long topicId
    ) {
        List<QuestionListDto> questions = topicId == null
                ? questionService.getAll()
                : questionService.getByTopic(topicId);
        return ResponseEntity.ok(questions);
    }

    /**
     * GET /api/v1/questions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionViewDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    /**
     * POST /api/v1/questions
     * Body: { "topicId": 1, "questionText": "...", "answerText": "..." }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid QuestionForm form) {
        questionService.create(form);
        return ResponseEntity.ok(Map.of("status", "created"));
    }

    /**
     * PUT /api/v1/questions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody @Valid QuestionForm form
    ) {
        questionService.update(id, form);
        return ResponseEntity.ok(Map.of("status", "updated"));
    }

    /**
     * DELETE /api/v1/questions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
