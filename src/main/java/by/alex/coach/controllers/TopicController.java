package by.alex.coach.controllers;


import by.alex.coach.dto.TopicForm;
import by.alex.coach.repository.TopicRepository;
import by.alex.coach.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/topics")
public class TopicController {
    private final TopicRepository topicRepository;
    private final TopicService topicService;

    @Autowired
    public TopicController(TopicRepository topicRepository, TopicService topicService) {
        this.topicRepository = topicRepository;
        this.topicService = topicService;
    }

    @GetMapping
    public String showTopics(Model model) {
        model.addAttribute("topics", topicRepository.findByParentIsNull());
        return "topics/all";
    }

    @GetMapping("/new")
    public String newTopicForm(Model model) {
        model.addAttribute("allTopics", topicRepository.findAll());
        model.addAttribute("topicForm", new TopicForm("", null));
        return "topics/new";
    }

    @PostMapping
    public String createTopic(@ModelAttribute @Valid TopicForm form,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTopics", topicRepository.findAll());
            return "topics/new";
        }

        topicService.createTopic(form);
        return "redirect:/topics";
    }
}
