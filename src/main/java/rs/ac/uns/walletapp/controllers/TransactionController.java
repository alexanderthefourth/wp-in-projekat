package rs.ac.uns.walletapp.controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.walletapp.dto.CreateTransactionDTO;
import rs.ac.uns.walletapp.dto.GetTransactionDTO;
import rs.ac.uns.walletapp.dto.TransactionMoveDTO;
import rs.ac.uns.walletapp.dto.TransactionMovedDTO;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.services.TransactionService;

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
}
