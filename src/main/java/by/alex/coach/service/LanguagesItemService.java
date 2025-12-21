package by.alex.coach.service;

import by.alex.coach.dto.LanguagesItemForm;
import by.alex.coach.models.LanguagesItem;
import by.alex.coach.models.Topic;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LanguagesItemService {
    private final LanguagesItemRepository languagesItemRepository;
    private final TopicRepository topicRepository;
    private final SrsReviewService reviewService;

    @Autowired
    public LanguagesItemService(LanguagesItemRepository languagesItemRepository, TopicRepository topicRepository, SrsReviewService reviewService) {
        this.languagesItemRepository = languagesItemRepository;
        this.topicRepository = topicRepository;
        this.reviewService = reviewService;
    }

    public void create(LanguagesItemForm form) {
        LanguagesItem item = new LanguagesItem();
        item.setTopic(Optional.ofNullable(form.topicId())
                .map(topicId -> topicRepository.findById(topicId)
                        .orElseThrow(() -> new IllegalArgumentException("Topic is not found")))
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
        //reviewService.getOrCreate("LANGUAGE_ITEM", item.getId());
    }
}
