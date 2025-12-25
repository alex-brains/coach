package by.alex.coach.controllers;

import by.alex.coach.dto.QuestionForm;
import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.repository.TopicRepository;
import by.alex.coach.service.QuestionService;
import by.alex.coach.service.TopicTreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final TopicTreeService treeService;

    @Autowired
    public QuestionController(QuestionService questionService, QuestionRepository questionRepository, TopicTreeService treeService) {
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.treeService = treeService;
    }

    /*@GetMapping
    public String allQuestions(Model model) {
        model.addAttribute("questions", questionRepository.findAll());
        return "questions/all";
    }*/

    @GetMapping
    public String allQuestions(@RequestParam(required = false) Long topicId, Model model) {
        if (topicId != null) {
            model.addAttribute("questions", questionRepository.findByTopicId(topicId));
        } else {
            model.addAttribute("questions", questionRepository.findAll());
        }
        model.addAttribute("allTopics", treeService.getAllAsTree());
        return "questions/all";
    }

    @GetMapping("/new")
    public String newQuestionForm(Model model) {
        model.addAttribute("form", new QuestionForm(null, "", ""));
        model.addAttribute("allTopics", treeService.getAllAsTree());
        return "questions/new";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid QuestionForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTopics", treeService.getAllAsTree());
            return "questions/new";
        }

        questionService.create(form);
        return "redirect:/questions";
    }
}
