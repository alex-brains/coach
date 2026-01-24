package by.alex.coach.controllers;

import by.alex.coach.dto.languages.LanguagesItemForm;
import by.alex.coach.repository.LanguagesItemRepository;
import by.alex.coach.service.LanguagesItemService;
import by.alex.coach.service.TopicTreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String allItems(@RequestParam(required = false) Long topicId, Model model) {
        model.addAttribute("items", topicId == null
                ? itemService.getAll()
                : itemService.getByTopic(topicId));

        model.addAttribute("allTopics", treeService.getAllAsTree());
        model.addAttribute("selectedTopicId", topicId);
        return "languages/all";
    }

    @GetMapping("/{id}")
    public String itemsById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("item", itemService.getItemsById(id));
        return "languages/itemsById";
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
