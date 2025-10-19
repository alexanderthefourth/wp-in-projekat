package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TxnTopDTO(
    Integer id,
    String name,
    BigDecimal amount,
    LocalDate dateOfExecution,
    Integer walletId,
    Integer categoryId,
    String categoryName
) {}
