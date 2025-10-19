package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRowDTO(
    Integer id,
    String transactionName,
    BigDecimal amount,
    String type,
    LocalDate dateOfExecution,
    Integer sourceWalletId,
    Integer targetWalletId
) {}
