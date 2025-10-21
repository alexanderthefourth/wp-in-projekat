package rs.ac.uns.walletapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.dto.CreateGoalDTO;
import rs.ac.uns.walletapp.dto.CurrencyDTO;
import rs.ac.uns.walletapp.dto.WalletCreatedDTO;
import rs.ac.uns.walletapp.dto.WalletDTO;
import rs.ac.uns.walletapp.model.Currency;
import rs.ac.uns.walletapp.model.Goal;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.uns.walletapp.repository.CurrencyRepository;
import rs.ac.uns.walletapp.repository.GoalRepository;
import rs.ac.uns.walletapp.repository.UserRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<WalletDTO> getAllForUser(int userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<Wallet> wallets = u.getWalletList();
        return wallets.stream().map(WalletDTO::new).toList();
    }

    public boolean deleteWallet(int id) {
        Optional<Wallet> walletOptional = walletRepository.findById(id);
        if (walletOptional.isPresent()) {
            walletRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public WalletCreatedDTO createWalletForUser(int userId, WalletDTO dto) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found " + userId));

        Wallet w = new Wallet();
        w.setName(dto.getName());
        w.setInitBal(dto.getInitBal());
        w.setCurrBal(dto.getCurrBal() != null ? dto.getCurrBal() : dto.getInitBal());
        w.setCurrency(currencyRepository.findByName(dto.getCurrency().getName())
                    .orElseThrow(() -> new IllegalArgumentException("Currency not found")));
        w.setCreationDate(dto.getCreatingDate());
        w.setArchived(dto.isArchived());
        w.setSavingsWallet(dto.isSavings());

        u.getWalletList().add(w);
        userRepository.save(u);

        return new WalletCreatedDTO(w.getId(), w.getName());
    }

    @Transactional(readOnly = true)
    public List<Wallet> getWalletsForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nepostojeći korisnik: " + userId));
        return user.getWalletList() == null ? List.of() : user.getWalletList();
    }

    public WalletDTO updateName(int id, String name) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Novcanik ne postoji."));

        wallet.setName(name);
        Wallet updatedWallet = walletRepository.save(wallet);
        return new WalletDTO(updatedWallet);
    }

    public WalletDTO updateSavings(int id, boolean savings) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Novcanik ne postoji."));

        wallet.setSavingsWallet(savings);
        Wallet updatedWallet = walletRepository.save(wallet);
        return new WalletDTO(updatedWallet);
    }

    public WalletDTO updateArchived(int id, boolean archived) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Novcanik ne postoji."));

        wallet.setArchived(archived);
        Wallet updatedWallet = walletRepository.save(wallet);
        return new WalletDTO(updatedWallet);
    }

    public WalletDTO updateCurrency(int id, String currencyName) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Novcanik ne postoji."));

        Currency currency = currencyRepository.findById(currencyName)
            .orElseThrow(() -> new RuntimeException("Valuta ne postoji."));

        wallet.setCurrency(currency);
        Wallet updatedWallet = walletRepository.save(wallet);
        return new WalletDTO(updatedWallet);
    }

    public BigDecimal viewCurrBal(int id){
        Optional<Wallet> pom = walletRepository.findById(id);
        if(pom.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet newWallet = pom.get();
        return newWallet.getCurrBal();
    }

    public List<Transfer> viewTransfers(int id){
        Optional<Wallet> pom = walletRepository.findById(id);
        if(pom.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet newWallet = pom.get();

        List<Transfer> allTransfers = new ArrayList<>();
        allTransfers.addAll(newWallet.getInTransfers());
        allTransfers.addAll(newWallet.getOutTransfers());

        return allTransfers;
    }

    public WalletDTO getWallet(int id){
        Optional<Wallet> pom = walletRepository.findById(id);
        if(pom.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet newWallet = pom.get();
        return new WalletDTO(newWallet);
    }
}


