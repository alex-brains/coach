package by.alex.coach.service;

import by.alex.coach.dto.question.*;
import by.alex.coach.models.Question;

import by.alex.coach.models.Topic;
import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionService {
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final SrsReviewService reviewService;

    public QuestionService(TopicRepository topicRepository,
                           QuestionRepository questionRepository,
                           SrsReviewService reviewService) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.reviewService = reviewService;
    }

    @Transactional
    public void create(QuestionForm form) {
        Question question = new Question();
        question.setTopic(Optional.ofNullable(form.topicId())
                .map(id -> topicRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + id)))
                .orElse(null));
        question.setQuestionText(form.questionText());
        question.setAnswerText(form.answerText());

        questionRepository.save(question);

        // Автоматически создаём SRS-запись — карточка сразу попадает в очередь изучения
        reviewService.getOrCreate("QUESTION", question.getId());
    }

    @Transactional
    public void update(Long id, QuestionForm form) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        q.setQuestionText(form.questionText());
        q.setAnswerText(form.answerText());
        q.setTopic(form.topicId() != null
                ? topicRepository.findById(form.topicId()).orElse(null)
                : null);
        // SRS запись не трогаем при редактировании вопроса
    }

    @Transactional
    public void delete(Long id) {
        // При удалении вопроса удаляем и SRS-запись (cascade или вручную)
        questionRepository.deleteById(id);
    }

    public List<QuestionListDto> getAll() {
        return questionRepository.findAllForList();
    }

    public List<QuestionListDto> getByTopic(Long topicId) {
        Topic root = topicRepository.findById(topicId).orElseThrow();
        Set<Long> topicIds = collectTopicIds(root);
        return questionRepository.findAllForListByTopics(topicIds);
    }

    public QuestionViewDto getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
        return new QuestionViewDto(
                q.getId(),
                q.getQuestionText(),
                autoSplit(q.getAnswerText()),
                q.getTopic() != null ? q.getTopic().getId() : null,
                q.getTopic() != null ? q.getTopic().getName() : null,
                q.getCreatedAt()
        );
    }

    // ─── Вспомогательные методы ──────────────────────────────────────────────

    /**
     * Рекурсивно собирает id топика и всех его потомков.
     * Используется для фильтрации вопросов по родительскому топику.
     *
     * Внимание: при глубоком дереве может вызвать N+1 запросов из-за lazy loading.
     * TODO: переписать на JOIN FETCH или рекурсивный CTE когда топиков станет много.
     */
    private Set<Long> collectTopicIds(Topic topic) {
        Set<Long> ids = new HashSet<>();
        ids.add(topic.getId());
        for (Topic child : topic.getChildren()) {
            ids.addAll(collectTopicIds(child));
        }
        return ids;
    }

    /**
     * Разбивает текст ответа на блоки: код и обычный текст.
     * Используется для красивого рендеринга ответа в шаблоне.
     */
    private List<AnswerBlockDto> autoSplit(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        List<AnswerBlockDto> blocks = new ArrayList<>();
        String[] lines = raw.split("\\n");
        StringBuilder current = new StringBuilder();
        boolean currentIsCode = false;

        for (String line : lines) {
            boolean isCode = looksLikeCode(line);

            if (!current.isEmpty() && isCode != currentIsCode) {
                blocks.add(new AnswerBlockDto(current.toString().trim(), currentIsCode));
                current.setLength(0);
            }

            current.append(line).append("\n");
            currentIsCode = isCode;
        }

        if (!current.isEmpty()) {
            blocks.add(new AnswerBlockDto(current.toString().trim(), currentIsCode));
        }

        return blocks;
    }

    private boolean looksLikeCode(String text) {
        String t = text.trim();
        return t.contains("{") || t.contains("}")
                || t.contains(";")
                || t.startsWith("class ") || t.startsWith("public ")
                || t.startsWith("private ") || t.startsWith("protected ")
                || t.startsWith("def ") || t.startsWith("function ")
                || t.startsWith("    ") || t.startsWith("\t");
    }
}
