package by.alex.coach.service;

import by.alex.coach.dto.topic.TopicForm;
import by.alex.coach.models.Topic;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional
    public void createTopic(TopicForm form) {
        Topic topic = new Topic();
        topic.setName(Optional.ofNullable(form.name())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Topic's name should not be empty")));
        topic.setParent(Optional.ofNullable(form.parentId())
                .map(parentId -> topicRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException("Parent topic is not found")))
                .orElse(null));

        topicRepository.save(topic);
    }
}
