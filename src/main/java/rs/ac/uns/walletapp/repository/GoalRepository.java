package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
