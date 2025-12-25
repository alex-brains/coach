package by.alex.coach.dto.question;

import java.time.Instant;

public record QuestionListDto(
        Long id,
        String question,
        String topicName,
        Instant createdAt
) {}
