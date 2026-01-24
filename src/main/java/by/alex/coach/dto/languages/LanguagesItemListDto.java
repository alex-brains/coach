package by.alex.coach.dto.languages;


import java.time.Instant;

public record LanguagesItemListDto(
        Long id,
        String translation,
        String topicName,
        Instant createdAt
) {
}
