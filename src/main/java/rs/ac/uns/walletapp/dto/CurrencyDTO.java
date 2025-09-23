package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrencyDTO {
    private String name;
    private BigDecimal value;

    public CurrencyDTO() {}

    public CurrencyDTO(rs.ac.uns.walletapp.model.Currency currency) {
        this.name = currency.getName();
        this.value = currency.getValue();
    }
}
