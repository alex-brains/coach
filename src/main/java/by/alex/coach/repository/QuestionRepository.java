package by.alex.coach.repository;

import by.alex.coach.dto.question.QuestionListDto;
import by.alex.coach.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("""
        select new by.alex.coach.dto.question.QuestionListDto(
            q.id,
            q.questionText,
            t.name,
            q.createdAt
        )
        from Question q
        join q.topic t
        """)
    List<QuestionListDto> findAllForList();

    @Query("""
        select new by.alex.coach.dto.question.QuestionListDto(
            q.id,
            q.questionText,
            t.name,
            q.createdAt
        )
        from Question q
        join q.topic t
        where t.id = :topicId
        """)
    List<QuestionListDto> findAllForListByTopic(@Param("topicId") Long topicId);

    @Query("""
    select new by.alex.coach.dto.question.QuestionListDto(
        q.id,
        q.questionText,
        t.name,
        q.createdAt
    )
    from Question q
    join q.topic t
    where t.id in :topicIds
""")
    List<QuestionListDto> findAllForListByTopics(
            @Param("topicIds") Collection<Long> topicIds
    );
}
