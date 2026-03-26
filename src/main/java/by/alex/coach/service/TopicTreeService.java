package by.alex.coach.service;

import by.alex.coach.dto.topic.TopicViewDto;
import by.alex.coach.models.Topic;
import by.alex.coach.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TopicTreeService {

    private final TopicRepository topicRepository;

    public TopicTreeService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    /**
     * Все топики в виде плоского дерева для dropdown.
     * type = null   → все топики
     * type = GENERAL  → только топики вопросов
     * type = LANGUAGE → только языковые топики
     */
    public List<TopicViewDto> getAllAsTree(String type) {
        List<TopicViewDto> result = new ArrayList<>();
        List<Topic> roots = type != null
                ? topicRepository.findByParentIsNullAndType(type)
                : topicRepository.findByParentIsNull();

        for (Topic root : roots) {
            walk(root, 0, result, type);
        }
        return result;
    }

    // Обратная совместимость — старый вызов без фильтра
    public List<TopicViewDto> getAllAsTree() {
        return getAllAsTree(null);
    }

    private void walk(Topic topic, int level, List<TopicViewDto> labels, String type) {
        labels.add(new TopicViewDto(
                topic.getId(),
                "- ".repeat(level) + topic.getName()
        ));
        for (Topic child : topic.getChildren()) {
            // Дочерние топики показываем всегда если родитель прошёл фильтр
            walk(child, level + 1, labels, null);
        }
    }
}
