package rs.ac.uns.walletapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.dto.DashboardStatsDTO;
import rs.ac.uns.walletapp.dto.DashboardTransactionDTO;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.repository.TransactionRepository;
import rs.ac.uns.walletapp.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DashboardStatsDTO getDashboardStats() {
        try {
            LocalDate monthAgo = LocalDate.now().minusMonths(1);
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            LocalDate today = LocalDate.now();
            LocalDate yesterday = LocalDate.now().minusDays(1);

            long totalUsers = userRepository.count();

            Long activeUsersCount = userRepository.countActiveUsersSince(monthAgo);
            long activeUsers = activeUsersCount != null ? activeUsersCount : 0;

            BigDecimal avgBalanceActive = userRepository.averageCurrBalOfActiveUsersSince(monthAgo);
            BigDecimal totalMoney = userRepository.totalMoneyInAllAccounts();

            List<Transaction> topLast30Days = transactionRepository.findTop10ByDateOfExecutionBetweenOrderByAmountDesc(thirtyDaysAgo, today);

            List<Transaction> topLastDay = transactionRepository.findTop10ByDateOfExecutionBetweenOrderByAmountDesc(yesterday, today);

            DashboardStatsDTO dto = new DashboardStatsDTO();
            dto.setTotalUsers(totalUsers);
            dto.setActiveUsers(activeUsers);
            dto.setAvgBalanceActive(avgBalanceActive != null ? avgBalanceActive : BigDecimal.ZERO);
            dto.setTotalMoney(totalMoney != null ? totalMoney : BigDecimal.ZERO);
            dto.setTop10Last30Days(convertToDashboardTransactionDTO(topLast30Days));
            dto.setTop10LastDay(convertToDashboardTransactionDTO(topLastDay));

            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            DashboardStatsDTO dto = new DashboardStatsDTO();
            dto.setTotalUsers(0);
            dto.setActiveUsers(0);
            dto.setAvgBalanceActive(BigDecimal.ZERO);
            dto.setTotalMoney(BigDecimal.ZERO);
            dto.setTop10Last30Days(List.of());
            dto.setTop10LastDay(List.of());
            return dto;
        }
    }

    private List<DashboardTransactionDTO> convertToDashboardTransactionDTO(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::convertToDashboardTransactionDTO)
                .collect(Collectors.toList());
    }

    private DashboardTransactionDTO convertToDashboardTransactionDTO(Transaction transaction) {
        DashboardTransactionDTO dto = new DashboardTransactionDTO();
        dto.setId(transaction.getId());
        dto.setName(transaction.getName());
        dto.setAmount(transaction.getAmount());
        dto.setDateOfExecution(transaction.getDateOfExecution());

        if (transaction.getCategory() != null) {
            dto.setCategoryName(transaction.getCategory().getName());
        } else {
            dto.setCategoryName("N/A");
        }

        return dto;
    }
}