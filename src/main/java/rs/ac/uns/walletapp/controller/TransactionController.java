package rs.ac.uns.walletapp.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.walletapp.dto.*;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("createTransaction")
    public ResponseEntity<GetTransactionDTO> makeTransaction(@RequestBody CreateTransactionDTO createTransactionDTO){
        try {
            GetTransactionDTO transaction = transactionService.addTransaction(createTransactionDTO);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("move")
    public ResponseEntity<?> moveTransaction(@RequestBody TransactionMoveDTO transactionMoveDTO) {
        try {
            TransactionMovedDTO movedTransaction = transactionService.moveTransaction(transactionMoveDTO);
            if (movedTransaction == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(movedTransaction);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("{id}/repeat")
    public ResponseEntity<Void> setRepeatActive(
        @PathVariable int id,
        @RequestParam boolean active
    ) {
        try {
            boolean ok = transactionService.setRepeatActive(id, active);
            return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("stop-all-repeats")
        public ResponseEntity<Void> stopAllRepeats(
        @RequestParam int userId,
        @RequestParam(required = false) Integer walletId
    ) {
        try {
            transactionService.stopAllRepeats(userId, walletId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TopExpenseDTO>> getTop10Expenses(@RequestParam int userId, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<TopExpenseDTO> topExpenses = transactionService.getTop10ExpensesForUserInPeriod(userId, start, end);
        return ResponseEntity.ok(topExpenses);
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(@RequestParam int userId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestParam(required = false) String username,        // Promenjeno iz userId u username
            @RequestParam(required = false) String categoryName,    // Promenjeno iz categoryId u categoryName
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String date) {
        try {
            LocalDate filterDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : null;

            List<Transaction> transactions = transactionService.filterTransactions(
                    username, categoryName, minAmount, maxAmount, filterDate
            );
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
