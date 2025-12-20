package by.alex.coach.dto;

public record QuestionForm(
        Long topicId,
        String questionText,
        String answerText
) {}
