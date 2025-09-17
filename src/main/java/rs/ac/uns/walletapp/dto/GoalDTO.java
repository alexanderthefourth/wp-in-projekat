package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GoalDTO {
    private int id;
    private String name;
    private BigDecimal targetAmount;
    private LocalDate deadline;
    private int walletId;
    private int userId;
    private BigDecimal currentAmount;
}

