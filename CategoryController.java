package com.example.categorymanagement.controller;

import com.example.categorymanagement.model.Category;
import com.example.categorymanagement.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service = new CategoryService();

    @GetMapping
    public String showCategories(Model model) {
        List<Category> categories = service.getAllCategories();
        model.addAttribute("categories", categories);

        // Calculations for Dashboard Stats Cards
        long total = categories.size();
        long mains = categories.stream().filter(c -> "MAIN".equals(c.getCategoryType())).count();
        long subs = total - mains;

        model.addAttribute("totalCount", total);
        model.addAttribute("mainCount", mains);
        model.addAttribute("subCount", subs);

        return "categoryList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Category> mains = service.getAllCategories().stream()
                .filter(c -> "MAIN".equals(c.getCategoryType()))
                .collect(Collectors.toList());
        model.addAttribute("mainCategories", mains);
        return "createCategory";
    }

    @PostMapping("/create")
    public String saveCategory(@RequestParam String categoryName,
                               @RequestParam String description,
                               @RequestParam String categoryType,
                               @RequestParam(required = false, defaultValue = "") String parentCategoryId) {
        String parentName = "";
        if (!parentCategoryId.isEmpty()) {
            Category p = service.getById(parentCategoryId);
            if (p != null) parentName = p.getCategoryName();
        }
        service.createCategory(categoryName, description, categoryType, parentCategoryId, parentName);
        return "redirect:/categories";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam String id, Model model) {
        Category c = service.getById(id);
        model.addAttribute("category", c);
        return "editCategory";
    }

    @PostMapping("/edit")
    public String processEdit(@RequestParam String categoryId,
                              @RequestParam String categoryName,
                              @RequestParam String description) {
        service.updateCategory(categoryId, categoryName, description);
        return "redirect:/categories";
    }

    @GetMapping("/delete")
    public String dropCategory(@RequestParam String id) {
        service.deleteCategory(id);
        return "redirect:/categories";
    }
}
