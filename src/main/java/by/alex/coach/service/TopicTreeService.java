package by.alex.coach.service;

import by.alex.coach.dto.topic.TopicViewDto;
import by.alex.coach.models.Topic;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TopicTreeService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicTreeService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicViewDto> getAllAsTree() {
        List<TopicViewDto> result = new ArrayList<>();
        for (Topic root: topicRepository.findByParentIsNull()) {
            walk(root, 0, result);
        }

        return result;
    }

    private void walk(Topic topic, int level, List<TopicViewDto> labels) {
        labels.add(new TopicViewDto(
                topic.getId(),
                "- ".repeat(level) + topic.getName()
        ));

        for (Topic child: topic.getChildren()) {
            walk(child, level + 1, labels);
        }
    }
}
