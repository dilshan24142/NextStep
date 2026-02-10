// src/main/java/com/example/StudentRegistrationSystem/controller/LostFoundWebController.java
package com.example.StudentRegistrationSystem.controller;

import com.example.StudentRegistrationSystem.entity.LostItem;
import com.example.StudentRegistrationSystem.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lost-found")
public class LostFoundWebController {

    @Autowired
    private LostItemService lostItemService;

    @GetMapping("/home")
    public String homePage(Model model) {
        long totalItems = lostItemService.countItems();
        long unclaimedItems = lostItemService.countUnclaimedItems();

        model.addAttribute("totalItems", totalItems);
        model.addAttribute("unclaimedItems", unclaimedItems);
        return "lost-found-home";
    }

    @GetMapping("/items")
    public String viewItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Page<LostItem> items;

        if (search != null && !search.isEmpty()) {
            items = lostItemService.searchItems(search, page, size);
            model.addAttribute("searchQuery", search);
        } else {
            items = lostItemService.getAllItems(page, size);
        }

        model.addAttribute("items", items.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("totalItems", items.getTotalElements());

        return "items-list";
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("item", new LostItem());
        return "report-item";
    }

    @PostMapping("/report")
    public String submitReport(@ModelAttribute LostItem item) {
        item.setDateFound(java.time.LocalDateTime.now());
        item.setClaimed(false);
        lostItemService.createItem(item);
        return "redirect:/lost-found/items";
    }
}