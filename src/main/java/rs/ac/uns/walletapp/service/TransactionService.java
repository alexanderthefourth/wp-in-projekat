package rs.ac.uns.walletapp.service;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.uns.walletapp.dto.*;
import rs.ac.uns.walletapp.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        transaction.setWallet(wallet);

        if (wallet.getTransactions() == null) wallet.setTransactions(new ArrayList<>());
        wallet.getTransactions().add(transaction);

        walletRepository.save(wallet);
        return new GetTransactionDTO(transaction);
    }

    public TransactionMovedDTO moveTransaction(TransactionMoveDTO dto) {
        var sourceWalletOpt = walletRepository.findById(dto.getSourceWalletId());
        var targetWalletOpt = walletRepository.findById(dto.getTargetWalletId());
        if (sourceWalletOpt.isEmpty() || targetWalletOpt.isEmpty()) return null;

        Wallet sourceWallet = sourceWalletOpt.get();
        Wallet targetWallet = targetWalletOpt.get();

        BigDecimal amount = dto.getAmount();
        if (sourceWallet.getCurrBal().compareTo(amount) < 0) return null;

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

        LocalDate execDate = dto.getDateOfExecution() != null ? dto.getDateOfExecution() : LocalDate.now();

        Transaction outTransaction = new Transaction();
        outTransaction.setName("Transfer u novčanik #" + targetWallet.getId());
        outTransaction.setAmount(amount.negate());
        outTransaction.setType(dto.getType());
        outTransaction.setDateOfExecution(execDate);
        outTransaction.setRepeatable(dto.isRepeatable());
        outTransaction.setActiveRepeat(dto.isActiveRepeat());
        outTransaction.setFrequency(dto.getFrequency());
        outTransaction.setWallet(sourceWallet);

        Transaction inTransaction = new Transaction();
        inTransaction.setName("Transfer iz novčanika #" + sourceWallet.getId());
        inTransaction.setAmount(convertedAmount);
        inTransaction.setType(dto.getType());
        inTransaction.setDateOfExecution(execDate);
        inTransaction.setRepeatable(dto.isRepeatable());
        inTransaction.setActiveRepeat(dto.isActiveRepeat());
        inTransaction.setFrequency(dto.getFrequency());
        inTransaction.setWallet(targetWallet);

        if (sourceWallet.getTransactions() == null) sourceWallet.setTransactions(new ArrayList<>());
        if (targetWallet.getTransactions() == null) targetWallet.setTransactions(new ArrayList<>());
        sourceWallet.getTransactions().add(outTransaction);
        targetWallet.getTransactions().add(inTransaction);

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        TransactionMovedDTO res = new TransactionMovedDTO();
        res.setSourceWalletId(sourceWallet.getId());
        res.setTargetWalletId(targetWallet.getId());
        res.setAmount(convertedAmount);
        res.setDateOfExecution(execDate);
        res.setTransactionName(outTransaction.getName() + " / " + inTransaction.getName());
        res.setType(dto.getType());
        return res;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void generateRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<Transaction> templates = transactionRepository.findAllByRepeatableTrueAndActiveRepeatTrue();

        for (Transaction tpl : templates) {
            if (tpl.getFrequency() == null || tpl.getFrequency().isBlank()) continue;

            LocalDate next = nextDate(tpl.getDateOfExecution(), tpl.getFrequency());
            if (next == null) continue;

            while (!next.isAfter(today)) {
                Transaction occ = new Transaction();
                occ.setName(tpl.getName());
                occ.setAmount(tpl.getAmount());
                occ.setType(tpl.getType());
                occ.setDateOfExecution(next);
                occ.setRepeatable(false);
                occ.setActiveRepeat(false);
                occ.setFrequency(null);
                occ.setUser(tpl.getUser());
                occ.setCategory(tpl.getCategory());
                occ.setWallet(tpl.getWallet());

                transactionRepository.save(occ);

                Wallet w = tpl.getWallet();
                if (w != null && occ.getAmount() != null && occ.getType() != null) {
                    switch (occ.getType()) {
                        case INCOME -> w.setCurrBal(w.getCurrBal().add(occ.getAmount()));
                        case EXPENSE -> w.setCurrBal(w.getCurrBal().subtract(occ.getAmount()));
                        default -> {}
                    }
                    walletRepository.save(w);
                }

                next = nextDate(next, tpl.getFrequency());
                if (next == null) break;
            }

            if (next != null && next.isAfter(today)) {
                tpl.setDateOfExecution(next);
                transactionRepository.save(tpl);
            }
        }
    }

    private LocalDate nextDate(LocalDate from, String freq) {
        if (from == null || freq == null) return null;
        switch (freq.trim().toUpperCase()) {
            case "DAILY":   return from.plusDays(1);
            case "WEEKLY":  return from.plusWeeks(1);
            case "MONTHLY": return from.plusMonths(1);
            default:        return null;
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

    public boolean setActiveRepeat(int id, boolean active) {
        Transaction t = transactionRepository.findById(id).orElse(null);
        if (t == null) return false;
        t.setActiveRepeat(active);
        transactionRepository.save(t);
        return true;
    }

    @Transactional
        public void deactivateAllRepeatsForUser(int userId) {
        List<Transaction> list = transactionRepository.findAllByUserIdAndRepeatableTrueAndActiveRepeatTrue(userId);
        for (Transaction t : list) {
            t.setActiveRepeat(false);
        }
        transactionRepository.saveAll(list);
    }

    @Transactional
    public void stopAllRepeats(int userId, Integer walletId) {
        if (walletId != null) {
            // Stop only for a specific wallet of that user
            List<Transaction> list = transactionRepository
                .findByUserIdAndWalletIdAndRepeatableTrueAndActiveRepeatTrue(userId, walletId);
            for (Transaction t : list) {
                t.setActiveRepeat(false);
            }
            transactionRepository.saveAll(list);
        } else {
            // Stop all for user
            List<Transaction> list = transactionRepository
                .findAllByUserIdAndRepeatableTrueAndActiveRepeatTrue(userId);
            for (Transaction t : list) {
                t.setActiveRepeat(false);
            }
            transactionRepository.saveAll(list);
        }
    }

    @Transactional
    public boolean setRepeatActive(int txId, boolean active) {
        return transactionRepository.findById(txId).map(tx -> {
            if (!tx.isRepeatable()) return false;
            tx.setActiveRepeat(active);
            transactionRepository.save(tx);
            return true;
        }).orElse(false);
    }

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void generateRecurringTransactionsEvery2Min() {
        List<Transaction> recurring = transactionRepository.findAllByRepeatableTrueAndActiveRepeatTrue();

        for (Transaction t : recurring) {
            String freq = (t.getFrequency() == null) ? "" : t.getFrequency().trim().toUpperCase();
            if (!"MIN2".equals(freq) && !"EVERY_2_MIN".equals(freq)) continue;

            Wallet w = t.getWallet();
            if (w == null) continue;

            Transaction nt = new Transaction();
            nt.setName(t.getName());
            nt.setAmount(t.getAmount());
            nt.setType(t.getType());
            nt.setDateOfExecution(LocalDate.now());
            nt.setRepeatable(t.isRepeatable());
            nt.setActiveRepeat(t.isActiveRepeat());
            nt.setFrequency(t.getFrequency());
            nt.setUser(t.getUser());
            nt.setCategory(t.getCategory());
            nt.setWallet(w);

            transactionRepository.save(nt);

            if (nt.getType() != null) {
                switch (nt.getType()) {
                    case INCOME -> w.setCurrBal(w.getCurrBal().add(nt.getAmount()));
                    case EXPENSE -> w.setCurrBal(w.getCurrBal().subtract(nt.getAmount()));
                    default -> {}
                }
            }
            walletRepository.save(w);
        }
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

    public List<Transaction> filterTransactions(Integer userId, Integer categoryId, BigDecimal minAmount, BigDecimal maxAmount, LocalDate date) {
        if (userId == null && categoryId == null && minAmount == null && maxAmount == null && date == null) {
            return transactionRepository.findAll();
        }
        return transactionRepository.filterTransactions(
                userId,
                categoryId,
                minAmount,
                maxAmount,
                date,
                date
        );
    }
}
