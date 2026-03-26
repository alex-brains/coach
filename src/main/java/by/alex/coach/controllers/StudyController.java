package by.alex.coach.controllers;

import by.alex.coach.models.SrsReview;
import by.alex.coach.service.LanguagesItemService;
import by.alex.coach.service.QuestionService;
import by.alex.coach.service.SrsReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/study")
public class StudyController {

    private final SrsReviewService reviewService;
    private final QuestionService questionService;
    private final LanguagesItemService languagesItemService;

    public StudyController(SrsReviewService reviewService,
                           QuestionService questionService,
                           LanguagesItemService languagesItemService) {
        this.reviewService = reviewService;
        this.questionService = questionService;
        this.languagesItemService = languagesItemService;
    }

    @GetMapping
    public String study(Model model) {
        SrsReview review = reviewService.nextDue(null, null).orElse(null);

        if (review == null) {
            model.addAttribute("dueCount", 0);
            return "study/empty";
        }

        if ("QUESTION".equals(review.getItemType())) {
            model.addAttribute("question", questionService.getQuestionById(review.getItemId()));
        } else {
            model.addAttribute("item", languagesItemService.getItemsById(review.getItemId()));
        }

        model.addAttribute("review", review);
        model.addAttribute("dueCount", reviewService.countDue(null));
        return "study/card";
    }

    @PostMapping("/{reviewId}/correct")
    public String correct(@PathVariable Long reviewId) {
        reviewService.markCorrect(reviewService.findById(reviewId));
        return "redirect:/study";
    }

    @PostMapping("/{reviewId}/incorrect")
    public String incorrect(@PathVariable Long reviewId) {
        reviewService.markIncorrect(reviewService.findById(reviewId));
        return "redirect:/study";
    }
}
