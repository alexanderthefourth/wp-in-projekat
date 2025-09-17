package rs.ac.uns.walletapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.repository.TransactionRepository;
import rs.ac.uns.walletapp.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    public List<Transaction> getFilteredTransactions(
            Integer userId,
            Integer categoryId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate startDate,
            LocalDate endDate,
            String sortBy,
            String sortDir
    ) {
        List<Transaction> transactions = transactionRepo.findAll();

        if (userId != null) {
            transactions.removeIf(t -> t.getUser() == null || t.getUser().getId() != userId);
        }

        if (categoryId != null) {
            transactions.removeIf(t -> t.getCategory() == null || t.getCategory().getId() != categoryId);
        }

        if (minAmount != null) {
            transactions.removeIf(t -> t.getAmount() == null || t.getAmount().compareTo(minAmount) < 0);
        }

        if (maxAmount != null) {
            transactions.removeIf(t -> t.getAmount() == null || t.getAmount().compareTo(maxAmount) > 0);
        }

        if (startDate != null) {
            transactions.removeIf(t -> t.getDateOfExecution() == null || t.getDateOfExecution().isBefore(startDate));
        }

        if (endDate != null) {
            transactions.removeIf(t -> t.getDateOfExecution() == null || t.getDateOfExecution().isAfter(endDate));
        }

        if ("amount".equalsIgnoreCase(sortBy)) {
            transactions.sort(Comparator.comparing(Transaction::getAmount, Comparator.nullsLast(BigDecimal::compareTo)));
        } else if ("dateOfExecution".equalsIgnoreCase(sortBy)) {
            transactions.sort(Comparator.comparing(Transaction::getDateOfExecution, Comparator.nullsLast(LocalDate::compareTo)));
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            List<Transaction> reversed = new ArrayList<>();
            for (int i = transactions.size() - 1; i >= 0; i--) {
                reversed.add(transactions.get(i));
            }
            transactions = reversed;
        }

        return transactions;
    }

    public long getTotalUsers() {
        return userRepo.count();
    }

    public long getActiveUsers() {
        LocalDate lastMonth = LocalDate.now().minusDays(30);
        return userRepo.countActiveUsersSince(lastMonth);
    }

    public BigDecimal getAvgMoneyOfActiveUsers() {
        LocalDate lastMonth = LocalDate.now().minusDays(30);
        BigDecimal avg = userRepo.averageCurrBalOfActiveUsersSince(lastMonth);
        return avg != null ? avg : BigDecimal.ZERO;
    }

    public BigDecimal getTotalMoneyInSystem() {
        BigDecimal sum = userRepo.totalMoneyInAllAccounts();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    public List<Transaction> getTop10Last30Days() {
        LocalDate today = LocalDate.now();
        return transactionRepo.findTop10ByDateOfExecutionBetweenOrderByAmountDesc(today.minusDays(30), today);
    }

    public List<Transaction> getTop10Today() {
        return transactionRepo.findTop10ByDateOfExecutionOrderByAmountDesc(LocalDate.now());
    }
}