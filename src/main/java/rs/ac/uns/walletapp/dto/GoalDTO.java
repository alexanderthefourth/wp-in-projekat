package rs.ac.uns.walletapp.dto;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GoalDTO {
    private int id;
    private String name;
    private BigDecimal targetAmount;
    private LocalDate deadline;

    public GoalDTO(Goal goal) {
        id = goal.getId();
        name = goal.getName();
        targetAmount = goal.getTargetAmount();
        deadline = goal.getDeadline();
    }
}

