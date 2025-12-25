package by.alex.coach.dto.question;

import java.time.Instant;

public record QuestionViewDto(
        Long id,
        String question,
        String answer,
        Long topicId,
        String topicName,
        Instant createdAt
) {}
