package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
