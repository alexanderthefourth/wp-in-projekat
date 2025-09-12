package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {
}
