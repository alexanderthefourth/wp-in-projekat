package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Type;

@Getter
@Setter
public class TransactionMoveDTO {
    private Integer sourceWalletId;
    private Integer targetWalletId;
    private BigDecimal amount;
    private String transactionName;
    private Type type;
    private LocalDate dateOfExecution;
    private boolean repeatable;
    private boolean activeRepeat;
    private String frequency;

    public TransactionMoveDTO(){}
}

