package rs.ac.uns.walletapp.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private static final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    static {
        exchangeRates.put("RSD-EUR", new BigDecimal("0.0085"));
        exchangeRates.put("EUR-RSD", new BigDecimal("117.6471"));
        exchangeRates.put("USD-EUR", new BigDecimal("0.91"));
        exchangeRates.put("EUR-USD", new BigDecimal("1.10"));
        exchangeRates.put("USD-RSD", new BigDecimal("111.1111"));
        exchangeRates.put("RSD-USD", new BigDecimal("0.009"));
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        String key = fromCurrency + "-" + toCurrency;
        BigDecimal rate = exchangeRates.get(key);

        if (rate == null) {
            throw new IllegalArgumentException("Zamena valuta nije moguca.");
        }

        return amount.multiply(rate);
    }
}
