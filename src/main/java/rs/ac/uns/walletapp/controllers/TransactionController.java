package rs.ac.uns.walletapp.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/createTransaction")
    public ResponseEntity<GetTransactionDTO> makeTransaction(@RequestBody CreateTransactionDTO createTransactionDTO){
        try {
            GetTransactionDTO transaction = transactionService.addTransaction(createTransactionDTO);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping()
    public ResponseEntity<TransactionMovedDTO> sendMoney(@RequestBody TransactionMoveDTO transactionMoveDTO){
        try{
            TransactionMovedDTO transaction = transactionService.moveTransaction(transactionMoveDTO);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/by-day")
    public ResponseEntity<List<Transaction>> getTransactionsByDay(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return new ResponseEntity<>(transactionService.getTransactionsByDay(localDate), HttpStatus.OK);
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<Transaction>> getTransactionsByMonth(@RequestParam int month, @RequestParam int year) {
        return new ResponseEntity<>(transactionService.getTransactionsByMonth(month, year), HttpStatus.OK);
    }

    @GetMapping("/by-quarter")
    public ResponseEntity<List<Transaction>> getTransactionsByQuarter(@RequestParam int quarter, @RequestParam int year) {
        return new ResponseEntity<>(transactionService.getTransactionsByQuarter(quarter, year), HttpStatus.OK);
    }
}
