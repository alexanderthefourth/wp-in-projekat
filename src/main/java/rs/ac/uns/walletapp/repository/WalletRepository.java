package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, String> {
}
