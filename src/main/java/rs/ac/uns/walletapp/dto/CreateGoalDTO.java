package rs.ac.uns.walletapp.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateGoalDTO {
    private int walletId;
    private String name;
    private BigDecimal targetAmount;
    private LocalDate deadline;

    public CreateGoalDTO(){}
}
