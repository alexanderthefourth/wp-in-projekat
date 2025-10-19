package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;

public record CategorySliceDTO(
    Integer categoryId,
    String categoryName,
    BigDecimal income,
    BigDecimal expense
) {}
