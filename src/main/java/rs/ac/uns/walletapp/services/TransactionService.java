package rs.ac.uns.walletapp.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.walletapp.dto.CreateTransactionDTO;
import rs.ac.uns.walletapp.dto.GetTransactionDTO;
import rs.ac.uns.walletapp.dto.TransactionMoveDTO;
import rs.ac.uns.walletapp.dto.TransactionMovedDTO;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.repository.TransactionRepository;
import rs.ac.uns.walletapp.repository.TransferRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;

@Service
public class TransactionService {

    private final TransferRepository transferRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyConversionService currencyConversionService;

    TransactionService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public GetTransactionDTO addTransaction(CreateTransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setName(transactionDTO.getName());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(transactionDTO.getType());
        transaction.setDateOfExecution(LocalDate.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new GetTransactionDTO(savedTransaction);
}

    public TransactionMovedDTO moveTransaction(TransactionMoveDTO transactionMoveDTO) {
        Optional<Wallet> sourceWalletOpt = walletRepository.findById(transactionMoveDTO.getSourceWalletId());
        Optional<Wallet> targetWalletOpt = walletRepository.findById(transactionMoveDTO.getTargetWalletId());
        
        if (sourceWalletOpt.isEmpty() || targetWalletOpt.isEmpty()) {
            return null;
        }

        Wallet sourceWallet = sourceWalletOpt.get();
        Wallet targetWallet = targetWalletOpt.get();

        BigDecimal amount = transactionMoveDTO.getAmount();

        if (sourceWallet.getCurrBal().compareTo(amount) < 0) {
            return null;
        }

        BigDecimal convertedAmount = amount;
        if (!sourceWallet.getCurrency().equals(targetWallet.getCurrency())) {
            convertedAmount = currencyConversionService.convert(amount, sourceWallet.getCurrency().getName(), targetWallet.getCurrency().getName());
        }

        sourceWallet.setCurrBal(sourceWallet.getCurrBal().subtract(amount));

        targetWallet.setCurrBal(targetWallet.getCurrBal().add(convertedAmount));

        Transfer t = new Transfer();
        t.setAmount(convertedAmount);
        t.setTransferDate(LocalDate.now());
        
        transferRepository.save(t);

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        return new TransactionMovedDTO(t);
    }

    public List<Transaction> getTransactionsByDay(LocalDate date) {
        return transactionRepository.findByDate(date);
    }

    public List<Transaction> getTransactionsByWeek(int week, int year) {
        return transactionRepository.findByWeekOfYear(week, year);
    }

    public List<Transaction> getTransactionsByMonth(int month, int year) {
        return transactionRepository.findByMonthAndYear(month, year);
    }

    public List<Transaction> getTransactionsByQuarter(int quarter, int year) {
        return transactionRepository.findByQuarterAndYear(quarter, year);
    }
}
