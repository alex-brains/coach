package by.alex.coach.service;

import by.alex.coach.dto.languages.LanguagesItemForm;
import by.alex.coach.dto.languages.LanguagesItemListDto;
import by.alex.coach.dto.languages.LanguagesItemViewDto;
import by.alex.coach.dto.question.QuestionListDto;
import by.alex.coach.dto.question.QuestionViewDto;
import by.alex.coach.models.LanguagesItem;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LanguagesItemService {
    private final LanguagesItemRepository languagesItemRepository;
    private final TopicRepository topicRepository;
    private final SrsReviewService reviewService;

    public LanguagesItemService(LanguagesItemRepository languagesItemRepository,
                                TopicRepository topicRepository,
                                SrsReviewService reviewService) {
        this.languagesItemRepository = languagesItemRepository;
        this.topicRepository = topicRepository;
        this.reviewService = reviewService;
    }

    public void create(LanguagesItemForm form) {
        LanguagesItem item = new LanguagesItem();
        item.setTopic(Optional.ofNullable(form.topicId())
                .map(id -> topicRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + id)))
                .orElse(null));
        item.setLanguage(form.language());
        item.setType(form.type());
        item.setWord(form.word().trim());
        item.setTranslation(form.translation().trim());
        item.setExample(Optional.ofNullable(form.example())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null));

        languagesItemRepository.save(item);

        // Автоматически создаём SRS-запись — слово сразу попадает в очередь изучения
        reviewService.getOrCreate("LANGUAGE_ITEM", item.getId());
    }

    @Transactional(readOnly = true)
    public List<LanguagesItemListDto> getAll() {
        return languagesItemRepository.findAllForList();
    }

    @Transactional(readOnly = true)
    public List<LanguagesItemListDto> getByTopic(Long topicId) {
        return languagesItemRepository.findAllForListByTopic(topicId);
    }

    @Transactional(readOnly = true)
    public LanguagesItemViewDto getItemsById(Long id) {
        LanguagesItem item = languagesItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LanguagesItem not found: " + id));
        return new LanguagesItemViewDto(
                item.getId(),
                item.getLanguage(),
                item.getType(),
                item.getWord(),
                item.getTranslation(),
                item.getExample(),
                item.getTopic() != null ? item.getTopic().getId() : null,
                item.getTopic() != null ? item.getTopic().getName() : null,
                item.getCreatedAt()
        );
    }
}
