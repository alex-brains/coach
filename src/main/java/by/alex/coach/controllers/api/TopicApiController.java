package by.alex.coach.controllers.api;

import by.alex.coach.dto.topic.TopicForm;
import by.alex.coach.dto.topic.TopicViewDto;
import by.alex.coach.service.TopicService;
import by.alex.coach.service.TopicTreeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicApiController {

    private final TopicService topicService;
    private final TopicTreeService topicTreeService;

    public TopicApiController(TopicService topicService, TopicTreeService topicTreeService) {
        this.topicService = topicService;
        this.topicTreeService = topicTreeService;
    }

    /**
     * GET /api/v1/topics               — все топики
     * GET /api/v1/topics?type=GENERAL  — только топики вопросов
     * GET /api/v1/topics?type=LANGUAGE — только языковые топики
     */
    @GetMapping
    public ResponseEntity<List<TopicViewDto>> getAll(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(topicTreeService.getAllAsTree(type));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid TopicForm form) {
        topicService.createTopic(form);
        return ResponseEntity.ok(Map.of("status", "created"));
    }
}
