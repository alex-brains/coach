package by.alex.coach.repository;

import by.alex.coach.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByParentIsNull();
    List<Topic> findByParentId(Long id);
}
