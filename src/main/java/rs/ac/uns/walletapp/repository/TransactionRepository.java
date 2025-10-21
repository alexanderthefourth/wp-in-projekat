package rs.ac.uns.walletapp.repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import rs.ac.uns.walletapp.dto.TransactionRowDTO;
import rs.ac.uns.walletapp.model.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByRepeatableTrueAndActiveRepeatTrue();

    List<Transaction> findAllByRepeatableTrueAndActiveRepeatTrueAndFrequencyIgnoreCase(String frequency);

    List<Transaction> findByUser_Id(Integer userId);

    List<Transaction> findByCategory_Id(Integer categoryId);

    List<Transaction> findByWallet_IdOrderByDateOfExecutionDesc(int walletId);
        List<Transaction> findByWallet_IdAndDateOfExecutionBetweenOrderByDateOfExecutionDesc(
        int walletId, LocalDate from, LocalDate to);

    List<Transaction> findByDateOfExecutionBetween(LocalDate start, LocalDate end);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.type = rs.ac.uns.walletapp.model.Type.EXPENSE " +
            "AND t.dateOfExecution BETWEEN :startDate AND :endDate " +
            "ORDER BY t.amount DESC " +
            "LIMIT 10")
    List<Transaction> findTop10ExpensesForUserInPeriod(int userId, LocalDate startDate, LocalDate endDate);

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

    List<Transaction> findAllByWalletIdAndRepeatableTrueAndActiveRepeatTrue(int walletId);

    List<Transaction> findAllByUserIdAndRepeatableTrueAndActiveRepeatTrue(int userId);
    List<Transaction> findByUserIdAndWalletIdAndRepeatableTrueAndActiveRepeatTrue(int userId, int walletId);

}
