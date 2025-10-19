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
public class SeriesPoint {
    private LocalDate periodStart;
    private BigDecimal income;
    private BigDecimal expense;
}
