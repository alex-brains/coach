package by.alex.coach.dto.languages;

import jakarta.validation.constraints.NotBlank;

public record LanguagesItemForm(
        Long topicId,

        @NotBlank(message = "Field should not be empty")
        String language,

        @NotBlank(message = "Field should not be empty")
        String type,

        @NotBlank(message = "Field should not be empty")
        String word,

        @NotBlank(message = "Field should not be empty")
        String translation,
        String example
) {}
