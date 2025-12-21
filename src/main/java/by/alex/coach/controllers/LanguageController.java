package by.alex.coach.controllers;

import by.alex.coach.dto.LanguagesItemForm;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.repository.TopicRepository;
import by.alex.coach.service.LanguagesItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/language-items")
public class LanguageController {
    private final LanguagesItemService itemService;
    private final LanguagesItemRepository itemRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public LanguageController(LanguagesItemService itemService, LanguagesItemRepository itemRepository, TopicRepository topicRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.topicRepository = topicRepository;
    }

    @GetMapping
    public String allItems(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        return "languages/all";
    }

    @GetMapping("/new")
    public String newItemForm(Model model) {
        model.addAttribute("form", new LanguagesItemForm(
                null, "EN", "WORD", "", "", ""
        ));
        model.addAttribute("allTopics", topicRepository.findAll());
        return "languages/new";
    }

    @PostMapping
    public String create(@ModelAttribute LanguagesItemForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTopics", topicRepository.findAll());
            return "languages/new";
        }

        itemService.create(form);
        return "redirect:/language-items";
    }
}
