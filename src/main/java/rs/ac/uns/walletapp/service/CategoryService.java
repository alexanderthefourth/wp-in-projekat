package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.Category;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category updateCategory(int id, Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }
        return null;
    }

    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getCategoriesForUser(int userId) {
        List<Category> categories = categoryRepository.findByPredefinedTrue();
        categories.addAll(categoryRepository.findByUserId(userId));
        return categories;
    }

    public Category createCategoryForUser(int userId, Category category) {
        category.setPredefined(false);
        User u = new User();
        u.setId(userId);
        category.setUser(u);
        return categoryRepository.save(category);
    }

    public void deleteUserCategory(int userId, int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null && !category.isPredefined() && category.getUser().getId() == userId) {
            categoryRepository.delete(category);
        }
    }
}

