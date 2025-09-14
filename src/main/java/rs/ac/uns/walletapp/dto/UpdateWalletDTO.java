package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Setter;
import lombok.Getter;

import rs.ac.uns.walletapp.model.Currency;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.Wallet;

@Setter
@Getter
public class UpdateWalletDTO {
    private int id;
    private String name;
    private BigDecimal initBal;
    private BigDecimal currBal;
    private Currency currency;
    private LocalDate creatingDate;
    private boolean archived;
    private boolean savings;
    private Goal goal;

    public UpdateWalletDTO(Wallet w){
        this.id = w.getId();
        this.name = w.getName();
        this.initBal = w.getInitBal();
        this.currBal = w.getCurrBal();
        this.currency = w.getCurrency();
        this.creatingDate = w.getCreationDate();
        this.archived = w.isArchived();
        this.goal = w.getGoal();
        this.savings = w.isSavingsWallet();
    }
}
