package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.dto.CreateGoalDTO;
import rs.ac.uns.walletapp.dto.GoalDTO;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.repository.GoalRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final WalletRepository walletRepository;

    public GoalService(GoalRepository goalRepository, WalletRepository walletRepository) {
        this.goalRepository = goalRepository;
        this.walletRepository = walletRepository;
    }

    public GoalDTO createGoal(CreateGoalDTO createGoalDTO) {
        Optional<Wallet> walletMaybe = walletRepository.findById(createGoalDTO.getWalletId());
        if(walletMaybe.isEmpty()) {
            throw new RuntimeException("Greska!");
        }

        Wallet wallet = walletMaybe.get();
        Goal goal = new Goal();
        goal.setName(createGoalDTO.getName());
        goal.setDeadline(createGoalDTO.getDeadline());
        goal.setTargetAmount(createGoalDTO.getTargetAmount());

        goal = goalRepository.save(goal);
        wallet.setGoal(goal);
        walletRepository.save(wallet);

        return new GoalDTO(goal);
    }

    public GoalDTO getGoalForWallet(int walletId) {
        Wallet w = walletRepository.findById(walletId).orElse(null);
        if (w == null || w.getGoal() == null) return null;
        return new GoalDTO(w.getGoal());
    }

}
