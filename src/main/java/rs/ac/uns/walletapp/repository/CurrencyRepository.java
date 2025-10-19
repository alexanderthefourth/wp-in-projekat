package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.Currency;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    Optional<Currency> findByName(String name);
}
