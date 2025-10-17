package rs.ac.uns.walletapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.walletapp.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.walletList w JOIN w.transactions t WHERE t.dateOfExecution >= :after")
    long countActiveUsersSince(LocalDate after);

    @Query("SELECT AVG(w.currBal) FROM User u JOIN u.walletList w JOIN w.transactions t WHERE t.dateOfExecution >= :after")
    BigDecimal averageCurrBalOfActiveUsersSince(LocalDate after);

    @Query("SELECT SUM(w.currBal) FROM User u JOIN u.walletList w")
    BigDecimal totalMoneyInAllAccounts();

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
