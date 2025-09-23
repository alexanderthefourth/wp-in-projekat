package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.dto.CategoryDTO;
import rs.ac.uns.walletapp.dto.CreateCategoryDTO;
import rs.ac.uns.walletapp.dto.UpdateCategoryDTO;
import rs.ac.uns.walletapp.model.Category;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.repository.CategoryRepository;
import rs.ac.uns.walletapp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    public List<CategoryDTO> getCategoriesForUser(int userId) {
        return categoryRepository.findByPredefinedTrueOrUserId(userId).stream().map(CategoryDTO::new).toList();
    }

    public CategoryDTO createCategoryForUser(CreateCategoryDTO createCategoryDTO) {
        Optional<User> userMaybe = userRepository.findById(createCategoryDTO.getUserId());
        if(userMaybe.isEmpty()) {
            throw new RuntimeException("luudii :DD");
        }

        Category newCategory = new Category();
        newCategory.setUser(userMaybe.get());

        newCategory.setPredefined(false);
        newCategory.setType(createCategoryDTO.getType());
        newCategory.setName(createCategoryDTO.getName());


        return new CategoryDTO(categoryRepository.save(newCategory));

    }

    public void deleteUserCategory(int userId, int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null && !category.isPredefined() && category.getUser().getId() == userId) {
            categoryRepository.delete(category);
        }
    }


    public CategoryDTO updateUserCategory(UpdateCategoryDTO updateCategoryDTO) {
        Optional<Category> categoryMaybe = categoryRepository.findById(updateCategoryDTO.getCategoryId());
        if(categoryMaybe.isEmpty()) {
            throw new RuntimeException("luudii :DD");
        }

        Category category = categoryMaybe.get();
        if(category.getUser().getId() != updateCategoryDTO.getUserId()) {
            throw new RuntimeException("luudii :DDDDD");
        }

        category.setName(updateCategoryDTO.getName());
        category.setType(updateCategoryDTO.getType());
        return new CategoryDTO(categoryRepository.save(category));
    }



    /*
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
    */
    public void deleteCategory(int id) {
        Optional<Category> categoryMaybe = categoryRepository.findById(id);
        if(categoryMaybe.isEmpty()) {
            throw new RuntimeException("luudii :DD");
        }
        categoryRepository.deleteById(id);
    }
}

