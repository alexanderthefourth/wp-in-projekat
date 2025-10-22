package rs.ac.uns.walletapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.CategoryDTO;
import rs.ac.uns.walletapp.dto.CreateCategoryDTO;
import rs.ac.uns.walletapp.dto.CreatePredefinedCategoryDTO;
import rs.ac.uns.walletapp.dto.UpdateCategoryDTO;
import rs.ac.uns.walletapp.model.Category;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("{userId}")
    public List<CategoryDTO> getUserCategories(@RequestParam int userId) {
        return categoryService.getCategoriesForUser(userId);
    }

    @PostMapping("createCategory")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        try {
            return ResponseEntity.ok(categoryService.createCategoryForUser(createCategoryDTO));
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage() ,HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("updateCategory")
    public ResponseEntity<?> updateCategory(UpdateCategoryDTO updateCategoryDTO ) {
        try{
            return ResponseEntity.ok(categoryService.updateUserCategory(updateCategoryDTO));
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage() ,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try{
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }
        catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage() ,HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/all")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }


    @PostMapping("/predefined")
    public ResponseEntity<?> createPredefinedCategory(@RequestBody CreatePredefinedCategoryDTO dto, HttpSession session) {
        try {
            User loggedUser = (User) session.getAttribute("user");
            if (loggedUser == null || !"ADMIN".equalsIgnoreCase((loggedUser.getRole().toString()))) {
                return new ResponseEntity<>("Nedozvoljen pristup — samo admin može dodati predefinisanu kategoriju.", HttpStatus.FORBIDDEN);
            }

            return ResponseEntity.ok(categoryService.createPredefinedCategory(dto));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

