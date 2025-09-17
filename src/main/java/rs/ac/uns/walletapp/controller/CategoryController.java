package rs.ac.uns.walletapp.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.model.Category;
import rs.ac.uns.walletapp.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public List<Category> getUserCategories(@RequestParam int userId) {
        return categoryService.getCategoriesForUser(userId);
    }

    @PostMapping
    public Category createCategory(@RequestParam int userId, @RequestBody Category category) {
        return categoryService.createCategoryForUser(userId, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id, @RequestParam int userId) {
        categoryService.deleteUserCategory(userId, id);
    }

    /*
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable int id, @RequestParam int userId, @RequestBody Category categoryDetails) {
        return categoryService.updateUserCategory(userId, id, categoryDetails);
    }
    */

    /*
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable int id, @RequestBody Category categoryInfo) {
        return categoryService.updateCategory(id, categoryInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
    }*/
}

