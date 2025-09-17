package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.dto.GoalDTO;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.repository.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<GoalDTO> getGoalsForUser(int userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        List<GoalDTO> dtos = new ArrayList<>();
        for (Goal g : goals) {
            dtos.add(GoalToDTO(g));
        }
        return dtos;
    }

    public GoalDTO getGoalById(int id, int userId) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal == null || goal.getUser().getId() != userId) {
            return null;
        }
        return GoalToDTO(goal);
    }

    public GoalDTO createGoal(int userId, GoalDTO goalDTO) {
        Goal goal = new Goal();
        goal.setName(goalDTO.getName());
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setDeadline(goalDTO.getDeadline());

        User user = new User();
        user.setId(userId);
        goal.setUser(user);

        Wallet wallet = new Wallet();
        wallet.setId(goalDTO.getWalletId());
        goal.setWallet(wallet);

        return GoalToDTO(goalRepository.save(goal));
    }

    public boolean deleteGoal(int id, int userId) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId() == userId) {
            goalRepository.delete(goal);
            return true;
        }
        return false;
    }

    private GoalDTO GoalToDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setName(goal.getName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setDeadline(goal.getDeadline());
        dto.setWalletId(goal.getWallet().getId());
        dto.setUserId(goal.getUser().getId());

        dto.setCurrentAmount(goal.getWallet().getCurrBal());

        return dto;
    }
}
