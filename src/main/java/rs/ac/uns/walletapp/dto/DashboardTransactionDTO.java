package rs.ac.uns.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTransactionDTO {
    private Integer id;
    private String name;
    private BigDecimal amount;
    private LocalDate dateOfExecution;
    private String categoryName;
}