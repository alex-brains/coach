package by.alex.coach.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionForm(
        Long topicId,

        @NotBlank(message = "Question text should not be empty")
        String questionText,

        @NotBlank(message = "Answer text should not be empty")
        String answerText
) {}
