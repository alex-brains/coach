package by.alex.coach.controllers;

import by.alex.coach.dto.LanguagesItemForm;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.repository.TopicRepository;
import by.alex.coach.service.LanguagesItemService;
import by.alex.coach.service.TopicTreeService;
import jakarta.validation.Valid;
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
    private final TopicTreeService treeService;

    @Autowired
    public LanguageController(LanguagesItemService itemService, LanguagesItemRepository itemRepository, TopicTreeService treeService) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.treeService = treeService;
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
        model.addAttribute("allTopics", treeService.getAllAsTree());
        return "languages/new";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid LanguagesItemForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTopics", treeService.getAllAsTree());
            return "languages/new";
        }

        itemService.create(form);
        return "redirect:/language-items";
    }
}
