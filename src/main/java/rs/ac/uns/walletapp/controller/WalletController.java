package rs.ac.uns.walletapp.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.walletapp.controller.WalletController.SimpleWalletDTO;
import rs.ac.uns.walletapp.dto.TransactionRowDTO;
import rs.ac.uns.walletapp.dto.WalletCreatedDTO;
import rs.ac.uns.walletapp.dto.WalletDTO;
import rs.ac.uns.walletapp.interfaces.ShowWalletInterface;
import rs.ac.uns.walletapp.model.Currency;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.repository.CurrencyRepository;
import rs.ac.uns.walletapp.repository.TransactionRepository;
import rs.ac.uns.walletapp.repository.UserRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;
import rs.ac.uns.walletapp.service.UserService;
import rs.ac.uns.walletapp.service.WalletService;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("walletCreate")
    @Transactional
    public ResponseEntity<WalletDTO> createWallet(@RequestParam int userId, @RequestBody WalletDTO dto) {
        User u = userRepository.findById(userId).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            Wallet w = new Wallet();
            w.setName(dto.getName());
            w.setInitBal(dto.getInitBal());
            w.setCurrBal(dto.getCurrBal() != null ? dto.getCurrBal() : dto.getInitBal());
            w.setCreationDate(dto.getCreatingDate());
            w.setArchived(dto.isArchived());
            w.setSavingsWallet(dto.isSavings());

            if (dto.getGoal() != null) {
                Goal g = new Goal();
                g.setName(dto.getGoal().getName());
                g.setTargetAmount(dto.getGoal().getTargetAmount());
                g.setDeadline(dto.getGoal().getDeadline());
                w.setGoal(g);
            }

            if (dto.getCurrency() != null && dto.getCurrency().getName() != null && !dto.getCurrency().getName().isBlank()) {
                Currency curr = currencyRepository.findById(dto.getCurrency().getName()).orElse(null);
                if (curr != null) w.setCurrency(curr);
            }

            walletRepository.save(w);

            if (u.getWalletList() == null) u.setWalletList(new ArrayList<>());
            u.getWalletList().add(w);
            userRepository.save(u);

            return new ResponseEntity<>(new WalletDTO(w), HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<SimpleWalletDTO>> listByUser(@RequestParam int userId) {
        var uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        var u = uOpt.get();
        var out = (u.getWalletList() == null) ? List.<SimpleWalletDTO>of()
            : u.getWalletList().stream()
                .map(w -> new SimpleWalletDTO(
                        w.getId(),
                        w.getName(),
                        w.getCurrency() != null ? w.getCurrency().getName() : null))
                .toList();

        return ResponseEntity.ok(out);
    }


    public static class SimpleWalletDTO {
        public int id;
        public String name;
        public String currencyName;
        public SimpleWalletDTO(int id, String name, String currencyName) {
            this.id = id; this.name = name; this.currencyName = currencyName;
        }
    }

    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllForUser(@RequestParam int userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();

        var dtos = userOpt.get().getWalletList()
                .stream()
                .map(WalletDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable int id) {
        try {
            var wallet = walletService.getWallet(id);
            if (wallet != null) return ResponseEntity.ok(wallet);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("{id}/deleteWallet")
    public ResponseEntity<Void> deleteWallet(@PathVariable int id) {
        try {
            boolean deleted = walletService.deleteWallet(id);
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/nameUpdate")
    public ResponseEntity<WalletDTO> updateName(@PathVariable int id, @RequestParam String name) {
        try {
            WalletDTO walletDTO = walletService.updateName(id, name);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/savingsUpdate")
    public ResponseEntity<WalletDTO> updateSavings(@PathVariable int id, @RequestParam boolean savings) {
        try {
            WalletDTO walletDTO = walletService.updateSavings(id, savings);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/archivedUpdate")
    public ResponseEntity<WalletDTO> updateArchived(@PathVariable int id, @RequestParam boolean archived) {
        try {
            WalletDTO walletDTO = walletService.updateArchived(id, archived);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/currencyUpdate")
    public ResponseEntity<WalletDTO> updateCurrency(@PathVariable int id, @RequestParam String currencyName) {
        try {
            WalletDTO walletDTO = walletService.updateCurrency(id, currencyName);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/viewCurrBal")
    public ResponseEntity<BigDecimal> viewCurrBalance(@PathVariable("id") int id){
        try{
            BigDecimal currBal = walletService.viewCurrBal(id);
            return new ResponseEntity<>(currBal, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/viewTransactions")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionRowDTO>> viewAllTransactions(@PathVariable("id") int id, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<Transaction> tx;

    if (from != null && to != null) {
        tx = transactionRepository.findByWallet_IdAndDateOfExecutionBetweenOrderByDateOfExecutionDesc(id, from, to);
    } else {
        tx = transactionRepository.findByWallet_IdOrderByDateOfExecutionDesc(id);
    }
        var rows = tx.stream().map(t -> new TransactionRowDTO(
                t.getId(),
                t.getName(),
                t.getAmount(),
                String.valueOf(t.getType()),
                t.getDateOfExecution(),
                null,
                null
        )).toList();

        return ResponseEntity.ok(rows);
    }
}

