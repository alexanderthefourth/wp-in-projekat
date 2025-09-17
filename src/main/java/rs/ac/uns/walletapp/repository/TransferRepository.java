package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.Optional;


public interface TransferRepository extends JpaRepository<Transfer, Integer> {
    Optional<Transfer> findByAmount(BigDecimal amount);
}
