package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByPredefinedTrue();
    List<Category> findByUserId(int userId);
}
