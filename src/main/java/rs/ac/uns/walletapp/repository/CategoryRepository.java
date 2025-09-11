package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
