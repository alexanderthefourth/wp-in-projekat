package rs.ac.uns.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTotal {
    private String categoryName;
    private BigDecimal income;
    private BigDecimal expense;
}
