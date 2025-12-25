package by.alex.coach.service;

import by.alex.coach.dto.question.QuestionForm;
import by.alex.coach.dto.question.QuestionListDto;
import by.alex.coach.dto.question.QuestionViewDto;
import by.alex.coach.models.Question;

import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class QuestionService {
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final SrsReviewService reviewService;

    @Autowired
    public QuestionService(TopicRepository topicRepository, QuestionRepository questionRepository, SrsReviewService reviewService) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.reviewService = reviewService;
    }

    @Transactional
    public void create(QuestionForm form) {
        Question question = new Question();
        question.setTopic(Optional.ofNullable(form.topicId())
                .map(topicId -> topicRepository.findById(topicId)
                        .orElseThrow(() -> new IllegalArgumentException("Topic is not found")))
                .orElse(null));
        question.setQuestionText(form.questionText());
        question.setAnswerText(form.answerText());

        questionRepository.save(question);
        //reviewService.getOrCreate("QUESTION", question.getId());
    }

    public List<QuestionListDto> getAll() {
        return questionRepository.findAllForList();
    }

    public List<QuestionListDto> getByTopic(Long topicId) {
        return questionRepository.findAllForListByTopic(topicId);
    }

    public QuestionViewDto getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        return new QuestionViewDto(
                q.getId(),
                q.getQuestionText(),
                q.getAnswerText(),
                q.getTopic() != null ? q.getTopic().getId() : null,
                q.getTopic() != null ? q.getTopic().getName() : null,
                q.getCreatedAt()
        );
    }

    @Transactional
    public void update(Long id, QuestionForm form) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        q.setQuestionText(form.questionText());
        q.setAnswerText(form.answerText());
        q.setTopic(form.topicId() != null
                ? topicRepository.findById(form.topicId()).orElse(null)
                : null
        );
    }

    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}
