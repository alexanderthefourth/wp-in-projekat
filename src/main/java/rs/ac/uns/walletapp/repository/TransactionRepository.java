package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Transaction;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.dateOfExecution = :date")
    List<Transaction> findByDate(LocalDate date);

    @Query("SELECT t FROM Transaction t WHERE FUNCTION('MONTH', t.dateOfExecution) = :month AND FUNCTION('YEAR', t.dateOfExecution) = :year")
    List<Transaction> findByMonthAndYear(int month, int year);
    
    @Query("SELECT t FROM Transaction t WHERE FUNCTION('QUARTER', t.dateOfExecution) = :quarter AND FUNCTION('YEAR', t.dateOfExecution) = :year")
    List<Transaction> findByQuarterAndYear(int quarter, int year);

    @Query("SELECT t FROM Transaction t WHERE YEAR(t.dateOfExecution) = :year AND WEEK(t.dateOfExecution) = :week")
    List<Transaction> findByWeekOfYear(int week, int year);
}
