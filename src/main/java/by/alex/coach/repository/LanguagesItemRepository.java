package by.alex.coach.repository;

import by.alex.coach.dto.languages.LanguagesItemListDto;
import by.alex.coach.dto.question.QuestionListDto;
import by.alex.coach.models.LanguagesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LanguagesItemRepository extends JpaRepository<LanguagesItem, Long> {
    List<LanguagesItem> findByLanguageAndType(String language, String type);

    @Query("""
    select new by.alex.coach.dto.languages.LanguagesItemListDto(
        l.id,
        l.translation,
        t.name,
        l.createdAt
    )
    from LanguagesItem l
    join l.topic t
""")
    List<LanguagesItemListDto> findAllForList();

    @Query("""
        select new by.alex.coach.dto.languages.LanguagesItemListDto(
            l.id,
            l.translation,
            t.name,
            l.createdAt
        )
        from LanguagesItem l
        join l.topic t
        where t.id = :topicId
        """)
    List<LanguagesItemListDto> findAllForListByTopic(@Param("topicId") Long topicId);
}
