package rs.ac.uns.walletapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.model.Currency;
import rs.ac.uns.walletapp.service.CurrencyService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    @PostMapping
    public ResponseEntity<?> createCurrency(@RequestBody Currency currency) {
        try {
            Currency saved = currencyService.createCurrency(currency);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{name}")
    public ResponseEntity<?> updateCurrency(@PathVariable String name, @RequestBody Currency currency) {
        try {
            Currency updated = currencyService.updateCurrency(name, currency);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCurrency(@PathVariable String name) {
        try {
            currencyService.deleteCurrency(name);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        try {
            BigDecimal result = currencyService.convertAmount(amount, from, to);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/rate")
    public ResponseEntity<?> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            BigDecimal rate = currencyService.getExchangeRate(from, to);
            return ResponseEntity.ok(rate);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}