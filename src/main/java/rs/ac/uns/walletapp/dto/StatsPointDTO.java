package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsPointDTO(
    LocalDate bucketDate,
    BigDecimal income,
    BigDecimal expense
) {}
