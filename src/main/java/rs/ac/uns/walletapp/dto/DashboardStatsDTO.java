package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DashboardStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private BigDecimal avgBalanceActive;
    private BigDecimal totalMoney;
    private List<DashboardTransactionDTO> top10Last30Days;
    private List<DashboardTransactionDTO> top10LastDay;
}