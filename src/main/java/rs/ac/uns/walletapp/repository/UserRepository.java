package rs.ac.uns.walletapp.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.walletapp.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.walletList w JOIN w.transactions t WHERE t.dateOfExecution >= :sinceDate")
    Long countActiveUsersSince(@Param("sinceDate") LocalDate sinceDate);

    @Query("SELECT COALESCE(AVG(w.currBal), 0) FROM User u JOIN u.walletList w JOIN w.transactions t WHERE t.dateOfExecution >= :after")
    BigDecimal averageCurrBalOfActiveUsersSince(@Param("after") LocalDate after);

    @Query("SELECT COALESCE(SUM(w.currBal), 0) FROM Wallet w")
    BigDecimal totalMoneyInAllAccounts();

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"walletList", "walletList.currency"})
    Optional<User> findById(Integer id);


}
