package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.Wallet;

@Setter
@Getter
public class WalletDTO {
    private int id;
    private String name;
    private BigDecimal initBal;
    private BigDecimal currBal;
    private CurrencyDTO currency;
    private LocalDate creatingDate;
    private boolean archived;
    private boolean savings;
    private CreateGoalDTO goal;

    public WalletDTO(Wallet w){
        this.id = w.getId();
        this.name = w.getName();
        this.initBal = w.getInitBal();
        this.currBal = w.getCurrBal();
        this.currency = new CurrencyDTO(w.getCurrency());
        this.creatingDate = w.getCreationDate();
        this.archived = w.isArchived();
        this.savings = w.isSavingsWallet();

        if (w.getGoal() != null) {
            Goal g = w.getGoal();
            this.goal = new CreateGoalDTO();
            this.goal.setName(g.getName());
            this.goal.setTargetAmount(g.getTargetAmount());
            this.goal.setDeadline(g.getDeadline());
        }
    }

    public WalletDTO(){}
}
