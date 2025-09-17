package rs.ac.uns.walletapp.dto;

import rs.ac.uns.walletapp.model.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DashboardDto {
    private long totalUsers;
    private long activeUsers;
    private BigDecimal averageMoneyActiveUsers;
    private BigDecimal totalMoneyInSystem;
    private List<Transaction> top10Last30Days;
    private List<Transaction> top10Today;
}
