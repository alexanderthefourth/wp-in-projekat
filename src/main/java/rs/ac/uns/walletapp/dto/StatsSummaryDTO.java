package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;

public record StatsSummaryDTO(
    BigDecimal income,
    BigDecimal expense,
    BigDecimal net
) {}
