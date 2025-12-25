package by.alex.coach.dto.question;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record QuestionViewDto(
        Long id,
        String question,
        List<AnswerBlockDto> answer,
        Long topicId,
        String topicName,
        Instant createdAt
) {
    public String getMergedCodeHtml() {
        StringBuilder sb = new StringBuilder();
        boolean inCode = false;

        for (AnswerBlockDto block : answer) {
            if (block.code()) {
                if (!inCode) {
                    sb.append("<pre><code class='language-java'>");
                    inCode = true;
                }
                sb.append(block.content()).append("\n");
            } else {
                // текстовый блок → закрываем код, если он был
                if (inCode) {
                    sb.append("</code></pre>");
                    inCode = false;
                }
                // вставляем как есть пустые строки
                if (block.content() != null) {
                    sb.append("<div style='white-space: pre-wrap;'>")
                            .append(block.content())
                            .append("</div>");
                }
            }
        }

        if (inCode) sb.append("</code></pre>");
        return sb.toString();
    }

    public String getAnswerTextForEdit() {
        return answer.stream()
                .map(AnswerBlockDto::content)
                .collect(Collectors.joining("\n"));
    }
}
