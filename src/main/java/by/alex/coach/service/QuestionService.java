package by.alex.coach.service;

import by.alex.coach.dto.QuestionForm;
import by.alex.coach.models.Question;

import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
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
}
