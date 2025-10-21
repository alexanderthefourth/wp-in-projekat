package rs.ac.uns.walletapp.service;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.uns.walletapp.dto.*;
import rs.ac.uns.walletapp.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.repository.TransactionRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyConversionService currencyConversionService;

    public GetTransactionDTO addTransaction(CreateTransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setName(transactionDTO.getName());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(transactionDTO.getType());
        transaction.setDateOfExecution(LocalDate.now());

        transaction.setRepeatable(transactionDTO.isRepeatable());
        transaction.setActiveRepeat(transactionDTO.isActiveRepeat());
        transaction.setFrequency(transactionDTO.getFrequency());

        Wallet wallet = walletRepository.findById(transactionDTO.getWalletId())
            .orElseThrow(() -> new RuntimeException("Novčanik ne postoji."));

        if (wallet.getTransactions() == null) {
            wallet.setTransactions(new ArrayList<>());
        }

        wallet.getTransactions().add(transaction);
        walletRepository.save(wallet);

        return new GetTransactionDTO(transaction);
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
            convertedAmount = currencyConversionService.convert(
                amount,
                sourceWallet.getCurrency().getName(),
                targetWallet.getCurrency().getName()
            );
        }

        sourceWallet.setCurrBal(sourceWallet.getCurrBal().subtract(amount));
        targetWallet.setCurrBal(targetWallet.getCurrBal().add(convertedAmount));

        Transaction outTransaction = new Transaction();
        outTransaction.setName("Transfer u novcanik #" + targetWallet.getId());
        outTransaction.setAmount(amount.negate());
        outTransaction.setType(transactionMoveDTO.getType());
        outTransaction.setDateOfExecution(LocalDate.now());
        outTransaction.setRepeatable(transactionMoveDTO.isRepeatable());
        outTransaction.setActiveRepeat(transactionMoveDTO.isActiveRepeat());
        outTransaction.setFrequency(transactionMoveDTO.getFrequency());

        Transaction inTransaction = new Transaction();
        inTransaction.setName("Transfer iz novcanika #" + sourceWallet.getId());
        inTransaction.setAmount(convertedAmount);
        inTransaction.setType(transactionMoveDTO.getType());
        inTransaction.setDateOfExecution(LocalDate.now());
        inTransaction.setRepeatable(transactionMoveDTO.isRepeatable());
        inTransaction.setActiveRepeat(transactionMoveDTO.isActiveRepeat());
        inTransaction.setFrequency(transactionMoveDTO.getFrequency());

        if (sourceWallet.getTransactions() == null)
            sourceWallet.setTransactions(new ArrayList<>());
        if (targetWallet.getTransactions() == null)
            targetWallet.setTransactions(new ArrayList<>());

        sourceWallet.getTransactions().add(outTransaction);
        targetWallet.getTransactions().add(inTransaction);

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        TransactionMovedDTO dto = new TransactionMovedDTO();
        dto.setSourceWalletId(sourceWallet.getId());
        dto.setTargetWalletId(targetWallet.getId());
        dto.setAmount(convertedAmount);
        dto.setDateOfExecution(LocalDate.now());
        dto.setTransactionName("Transfer iz novcanika #" + sourceWallet.getId() + " u novcanik #" + targetWallet.getId());
        dto.setType(transactionMoveDTO.getType());

        return dto;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void generateRecurringTransactions() {
        List<Transaction> recurringTransactions = transactionRepository.findAllByRepeatableTrueAndActiveRepeatTrue();

        for (Transaction t : recurringTransactions) {
            if (shouldCreateNextTransaction(t)) {
                Transaction newTransaction = new Transaction();
                newTransaction.setName(t.getName());
                newTransaction.setAmount(t.getAmount());
                newTransaction.setType(t.getType());
                newTransaction.setDateOfExecution(LocalDate.now());
                newTransaction.setRepeatable(t.isRepeatable());
                newTransaction.setActiveRepeat(t.isActiveRepeat());
                newTransaction.setFrequency(t.getFrequency());
                newTransaction.setUser(t.getUser());
                newTransaction.setCategory(t.getCategory());

                transactionRepository.save(newTransaction);
            }
        }
    }

    private boolean shouldCreateNextTransaction(Transaction t) {
        LocalDate lastExecution = t.getDateOfExecution();
        String freq = t.getFrequency();

        switch (freq.toUpperCase()) {
            case "DAILY":
                return lastExecution.plusDays(1).isEqual(LocalDate.now());
            case "WEEKLY":
                return lastExecution.plusWeeks(1).isEqual(LocalDate.now());
            case "MONTHLY":
                return lastExecution.plusMonths(1).isEqual(LocalDate.now());
            default:
                return false;
        }
    }
 
    public static Specification<Transaction> hasUser(Integer userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Transaction> hasCategory(Integer categoryId) {
        return (root, query, cb) ->
                categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Transaction> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            if (min != null && max != null) return cb.between(root.get("amount"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("amount"), min);
            return cb.lessThanOrEqualTo(root.get("amount"), max);
        };
    }



    public static Specification<Transaction> dateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();
            if (start != null && end != null) return cb.between(root.get("dateOfExecution"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("dateOfExecution"), start);
            return cb.lessThanOrEqualTo(root.get("dateOfExecution"), end);
        };
    }

    public List<TopExpenseDTO> getTop10ExpensesForUserInPeriod(int userId, LocalDate start, LocalDate end) {
        List<Transaction> expenses = transactionRepository.findTop10ExpensesForUserInPeriod(userId, start, end);
        List<TopExpenseDTO> result = new ArrayList<>();

        for (Transaction t : expenses) {
            result.add(new TopExpenseDTO(
                    t.getName(),
                    t.getAmount(),
                    t.getDateOfExecution(),
                    t.getCategory() != null ? t.getCategory().getName() : "N/A"
            ));
        }
        return result;
    }

    public List<Transaction> getTransactionsByUser(int userId) {
        return transactionRepository.findByUser_Id(userId);
    }

    public List<Transaction> filterTransactions(
            String username,
            String categoryName,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate date) {

        return transactionRepository.filterTransactions(
                username,
                categoryName,
                minAmount,
                maxAmount,
                date,
                date
        );
    }
}
