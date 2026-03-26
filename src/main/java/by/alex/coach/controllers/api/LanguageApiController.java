package by.alex.coach.controllers.api;

import by.alex.coach.dto.languages.LanguagesItemForm;
import by.alex.coach.dto.languages.LanguagesItemListDto;
import by.alex.coach.dto.languages.LanguagesItemViewDto;
import by.alex.coach.service.LanguagesItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/language-items")
public class LanguageApiController {
    private final LanguagesItemService languagesItemService;

    public LanguageApiController(LanguagesItemService languagesItemService) {
        this.languagesItemService = languagesItemService;
    }

    /**
     * GET /api/v1/language-items
     * GET /api/v1/language-items?topicId=4
     */
    @GetMapping
    public ResponseEntity<List<LanguagesItemListDto>> getAll(
            @RequestParam(required = false) Long topicId
    ) {
        List<LanguagesItemListDto> items = topicId == null
                ? languagesItemService.getAll()
                : languagesItemService.getByTopic(topicId);
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/v1/language-items/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LanguagesItemViewDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(languagesItemService.getItemsById(id));
    }

    /**
     * POST /api/v1/language-items
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid LanguagesItemForm form) {
        languagesItemService.create(form);
        return ResponseEntity.ok(Map.of("status", "created"));
    }
}
