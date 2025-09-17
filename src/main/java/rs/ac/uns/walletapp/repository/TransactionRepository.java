package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query(value = """
    SELECT * 
    FROM transaction t
    ORDER BY t.date_of_execution
    """, nativeQuery = true)
    List<Transaction> findAllOrderedByFullDate();

    @Query(value = """
    SELECT * 
    FROM transaction t
    ORDER BY EXTRACT(WEEK FROM t.date_of_execution)
    """, nativeQuery = true)
    List<Transaction> findAllOrderedByWeek();

    @Query(value = """
    SELECT * 
    FROM transaction t
    ORDER BY EXTRACT(YEAR FROM t.date_of_execution), EXTRACT(MONTH FROM t.date_of_execution)
    """, nativeQuery = true)
    List<Transaction> findAllOrderedByMonthAndYear();
    
    @Query(value = """
    SELECT * 
    FROM transaction t
    ORDER BY EXTRACT(QUARTER FROM t.date_of_execution)
    """, nativeQuery = true)
    List<Transaction> findAllOrderedByQuarter();

    @Query(value = """
    SELECT * 
    FROM transaction t
    ORDER BY EXTRACT(YEAR FROM t.date_of_execution)
    """, nativeQuery = true)
    List<Transaction> findAllOrderedByYear();


    List<Transaction> findAllByRepeatableTrueAndActiveRepeatTrue();
}
