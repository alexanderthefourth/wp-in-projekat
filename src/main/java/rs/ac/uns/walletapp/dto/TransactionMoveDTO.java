package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionMoveDTO {
    private Integer sourceWalletId;
    private Integer targetWalletId;
    private BigDecimal amount;
    private String transactionName;
    private String type;
    private LocalDate dateOfExecution;
}

