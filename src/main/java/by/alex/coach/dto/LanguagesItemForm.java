package by.alex.coach.dto;

public record LanguagesItemForm(
        Long topicId,
        String language,
        String type,
        String word,
        String translation,
        String example
) {}
