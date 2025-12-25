package by.alex.coach.dto.topic;

import jakarta.validation.constraints.NotBlank;

public record TopicForm(
        @NotBlank(message = "Topic's name should not be empty")
        String name,
        Long parentId
) {}
