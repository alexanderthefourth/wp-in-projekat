package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Transfer;

@Getter
@Setter
public class TransactionMovedDTO {
    private Integer sourceWalletId;
    private Integer targetWalletId;
    private BigDecimal amount;
    private String transactionName;
    private String type;
    private LocalDate dateOfExecution;

    public TransactionMovedDTO(Transfer t){
        this.amount = t.getAmount();
        this.dateOfExecution = t.getTransferDate();
    }
}

