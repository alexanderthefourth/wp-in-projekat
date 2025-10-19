package rs.ac.uns.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TxItem {
    private int id;
    private String name;
    private BigDecimal amount;
    private LocalDate dateOfExecution;
    private String categoryName;
    private Integer walletId;
}
