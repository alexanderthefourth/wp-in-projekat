package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.Currency;
import rs.ac.uns.walletapp.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency createCurrency(Currency currency) {
        if (currencyRepository.existsById(currency.getName())) {
            throw new RuntimeException("valuta vec postoji");
        }

        if (currency.getValue() == null || currency.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("vrednost mora biti veca od nule");
        }

        return currencyRepository.save(currency);
    }

    public Currency updateCurrency(String name, Currency currency) {
        Optional<Currency> existing = currencyRepository.findById(name);
        if (existing.isEmpty()) {
            throw new RuntimeException("valuta nije pronadjena");
        }

        if ("RSD".equals(name)) {
            throw new RuntimeException("vrednost RSD se ne moze editovati");
        }

        if (currency.getValue() == null || currency.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("vrednost mora biti veca od nule");
        }

        Currency toUpdate = existing.get();
        toUpdate.setValue(currency.getValue());
        return currencyRepository.save(toUpdate);
    }

    public void deleteCurrency(String name) {
        if ("RSD".equals(name)) {
            throw new RuntimeException("RSD valuta se ne može obrisati.");
        }

        if (!currencyRepository.existsById(name)) {
            throw new RuntimeException("Valuta nije pronađena.");
        }

        currencyRepository.deleteById(name);
    }

    public Optional<Currency> findByName(String name) {
        return currencyRepository.findByName(name);
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        Optional<Currency> from = currencyRepository.findByName(fromCurrency);
        Optional<Currency> to = currencyRepository.findByName(toCurrency);

        if (from.isEmpty() || to.isEmpty()) {
            throw new RuntimeException("Valuta nije pronađena.");
        }

        return from.get().getValue().divide(to.get().getValue(), 6, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(rate);
    }
}