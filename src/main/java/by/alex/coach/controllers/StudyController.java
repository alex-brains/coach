package by.alex.coach.controllers;

import by.alex.coach.models.SrsReview;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.repository.SrsReviewRepository;
import by.alex.coach.service.SrsReviewSelectionService;
import by.alex.coach.service.SrsReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study")
public class StudyController {
    private final SrsReviewSelectionService selectionService;
    private final QuestionRepository questionRepository;
    private final LanguagesItemRepository languageRepository;
    private final SrsReviewService reviewService;
    private final SrsReviewRepository reviewRepository;

    public StudyController(
            SrsReviewSelectionService selectionService,
            QuestionRepository questionRepository,
            LanguagesItemRepository languageRepository,
            SrsReviewService reviewService, SrsReviewRepository reviewRepository
    ) {
        this.selectionService = selectionService;
        this.questionRepository = questionRepository;
        this.languageRepository = languageRepository;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public String study(Model model) {
        SrsReview review = selectionService.nextDue()
                .orElse(null);

        if (review == null) {
            return "study/empty";
        }

        if (review.getItemType().equals("QUESTION")) {
            model.addAttribute("question",
                    questionRepository.findById(review.getItemId()).orElseThrow());
        } else {
            model.addAttribute("item",
                    languageRepository.findById(review.getItemId()).orElseThrow());
        }

        model.addAttribute("review", review);
        return "study/card";
    }

    @PostMapping("/{reviewId}/correct")
    public String correct(@PathVariable Long reviewId) {
        reviewService.markCorrect(
                reviewRepository.findById(reviewId).orElseThrow()
        );
        return "redirect:/study";
    }

    @PostMapping("/{reviewId}/incorrect")
    public String incorrect(@PathVariable Long reviewId) {
        reviewService.markIncorrect(
                reviewRepository.findById(reviewId).orElseThrow()
        );
        return "redirect:/study";
    }
}
