package rs.ac.uns.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TopExpenseDTO {
    private String name;
    private BigDecimal amount;
    private LocalDate dateOfExecution;
    private String categoryName;
}
