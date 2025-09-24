package rs.ac.uns.walletapp.controller;

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


    @GetMapping("by-day")
    public Map<LocalDate, List<Transaction>> getTransactionsGroupedByDay() {
        return transactionService.getTransactionsGroupedByDay();
    }

    @GetMapping("by-month")
    public Map<YearMonth, List<Transaction>> getTransactionsGroupedByMonth() {
        return transactionService.getTransactionsGroupedByMonth();
    }

    @GetMapping("by-week")
    public Map<Integer, List<Transaction>> getTransactionsGroupedByWeek() {
        return transactionService.getTransactionsGroupedByWeek();
    }

    @GetMapping("by-year")
    public Map<Integer, List<Transaction>> getTransactionsGroupedByYear() {
        return transactionService.getTransactionsGroupedByYear();
    }

    @GetMapping("by-quarter")
    public Map<Integer, List<Transaction>> getTransactionsGroupedByQuarter() {
        return transactionService.getTransactionsGroupedByQuarter();
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TopExpenseDTO>> getTop10Expenses(@RequestParam int userId, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<TopExpenseDTO> topExpenses = transactionService.getTop10ExpensesForUserInPeriod(userId, start, end);
        return ResponseEntity.ok(topExpenses);
    }

}
