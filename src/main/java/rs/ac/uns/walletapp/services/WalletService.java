package rs.ac.uns.walletapp.services;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.walletapp.repository.CurrencyRepository;
import rs.ac.uns.walletapp.repository.GoalRepository;
import rs.ac.uns.walletapp.repository.WalletRepository;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    public boolean deleteWallet(int id) {
        Optional<Wallet> walletOptional = walletRepository.findById(id);
        if (walletOptional.isPresent()) {
            walletRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public WalletCreatedDTO createWallet(WalletDTO walletDTO){
        Wallet newWallet = new Wallet();
        newWallet.setName(walletDTO.getName());
        newWallet.setInitBal(walletDTO.getInitBal());
        newWallet.setCurrBal(walletDTO.getCurrBal());
        newWallet.setCreationDate(LocalDate.now());
        newWallet.setTransactions(null);
        newWallet.setArchived(false);
        newWallet.setInTransfers(null);
        newWallet.setOutTransfers(null);

        CurrencyDTO currencyDTO = walletDTO.getCurrency();

        Currency currency = currencyRepository.findById(currencyDTO.getName())
            .orElseGet(() -> {
                Currency c = new Currency();
                c.setName(currencyDTO.getName());
                c.setValue(currencyDTO.getValue());
                return currencyRepository.save(c);
            });

        newWallet.setCurrency(currency);

        if (walletDTO.getGoal() != null) {
            CreateGoalDTO goalDTO = walletDTO.getGoal();
            Goal goal = new Goal();
            goal.setName(goalDTO.getName());
            goal.setTargetAmount(goalDTO.getTargetAmount());
            goal.setDeadline(goalDTO.getDeadline());

            Goal savedGoal = goalRepository.save(goal);
            newWallet.setGoal(savedGoal);
        }

        Wallet savedWallet = walletRepository.save(newWallet);
        return new WalletCreatedDTO(savedWallet);
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


