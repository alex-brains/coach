package by.alex.coach.controllers;

import by.alex.coach.dto.question.QuestionForm;
import by.alex.coach.dto.question.QuestionViewDto;
import by.alex.coach.repository.QuestionRepository;
import by.alex.coach.service.QuestionService;
import by.alex.coach.service.TopicTreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping
    public String allQuestions(@RequestParam(required = false) Long topicId, Model model) {
        model.addAttribute("questions", topicId == null
                    ? questionService.getAll()
                    : questionService.getByTopic(topicId));;

        model.addAttribute("allTopics", treeService.getAllAsTree());
        model.addAttribute("selectedTopicId", topicId);
        return "questions/all";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
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

    @GetMapping("/{id}")
    public String questionById(@PathVariable Long id, Model model) {
        model.addAttribute("question", questionService.getQuestionById(id));
        return "questions/questionById";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        QuestionViewDto dto = questionService.getQuestionById(id);
        model.addAttribute("form", new QuestionForm(dto.topicId(), dto.question(), dto.answer()));
        model.addAttribute("questionId", id);
        model.addAttribute("allTopics", treeService.getAllAsTree());
        return "questions/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute @Valid QuestionForm form, BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTopics", treeService.getAllAsTree());
            model.addAttribute("questionId", id);
            return "questions/edit";
        }

        questionService.update(id, form);
        return "redirect:/questions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        questionService.delete(id);
        return "redirect:/questions";
    }
}
