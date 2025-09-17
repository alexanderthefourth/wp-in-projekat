package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.Wallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletCreatedDTO {
    private int id;
    private String name;
    private BigDecimal initBal;
    private BigDecimal currBal;
    private CurrencyDTO currency;
    private LocalDate creatingDate;
    private boolean archived;
    private boolean savings;
    private Goal goal;

    public WalletCreatedDTO(Wallet w){
        this.id = w.getId();
        this.name = w.getName();
        this.initBal = w.getInitBal();
        this.currBal = w.getCurrBal();
        this.currency = new CurrencyDTO(w.getCurrency());
        this.creatingDate = w.getCreationDate();
        this.archived = w.isArchived();
        this.goal = w.getGoal();
        this.savings = w.isSavingsWallet();
    }

    public WalletCreatedDTO(){}
}
