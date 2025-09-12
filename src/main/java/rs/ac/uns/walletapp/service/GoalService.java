package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.repository.GoalRepository;

import java.util.List;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }


    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }


    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(int id) {
        return goalRepository.findById(id).orElse(null);
    }

    public Goal updateGoal(int id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null) {
            goal.setName(goalDetails.getName());
            goal.setTargetAmount(goalDetails.getTargetAmount());
            goal.setDeadline(goalDetails.getDeadline());
            return goalRepository.save(goal);
        }
        return null;
    }

    public void deleteGoal(int id) {
        goalRepository.deleteById(id);
    }
}

