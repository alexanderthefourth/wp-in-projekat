package rs.ac.uns.walletapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.walletapp.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUser_Id(Integer userId);
    List<Transaction> findByCategory_Id(Integer categoryId);

    List<Transaction> findByAmountBetween(BigDecimal min, BigDecimal max);
    List<Transaction> findByDateOfExecutionBetween(LocalDate start, LocalDate end);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:userId IS NULL OR t.user.id = :userId) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) AND " +
            "(:startDate IS NULL OR t.dateOfExecution >= :startDate) AND " +
            "(:endDate IS NULL OR t.dateOfExecution <= :endDate)")
    List<Transaction> filterTransactions(
            Integer userId,
            Integer categoryId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Transaction> findTop10ByDateOfExecutionBetweenOrderByAmountDesc(LocalDate start, LocalDate end);

    List<Transaction> findTop10ByDateOfExecutionOrderByAmountDesc(LocalDate date);
}
