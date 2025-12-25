package by.alex.coach.dto.question;

public record AnswerBlockDto(
        String content,
        boolean code    // true -> <pre><code>, false -> просто <p>
) {}
