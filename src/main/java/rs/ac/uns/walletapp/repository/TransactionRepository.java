package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
