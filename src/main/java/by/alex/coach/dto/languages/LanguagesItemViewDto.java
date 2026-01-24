package by.alex.coach.dto.languages;

import by.alex.coach.dto.question.AnswerBlockDto;

import java.time.Instant;
import java.util.List;

public record LanguagesItemViewDto(
        Long id,
        String language,
        String type,
        String word,
        String translation,
        String example,
        Long topicId,
        String topicName,
        Instant createdAt
) {
}
