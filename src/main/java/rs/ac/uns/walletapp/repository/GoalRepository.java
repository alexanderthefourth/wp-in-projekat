package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    Goal findByWalletId(int walletId);
    List<Goal> findByUserId(int userId);
}
