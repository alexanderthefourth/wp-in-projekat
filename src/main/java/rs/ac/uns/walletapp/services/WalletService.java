package rs.ac.uns.walletapp.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import rs.ac.uns.walletapp.model.Wallet;
import rs.ac.uns.walletapp.dto.UpdateWalletDTO;
import rs.ac.uns.walletapp.dto.WalletCreatedDTO;
import rs.ac.uns.walletapp.dto.WalletDTO;
import rs.ac.uns.walletapp.model.Transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.walletapp.repository.WalletRepository;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public void deleteWallet(int id){
        if(walletRepository.existsById(id)){
            walletRepository.deleteById(id);
        }
        //else ako ne postoji
    }

    public WalletCreatedDTO createWallet(WalletDTO walletDTO){
        Wallet newWallet = new Wallet();
        newWallet.setName(walletDTO.getName());
        newWallet.setInitBal(walletDTO.getInitBal());
        newWallet.setCurrency(walletDTO.getCurrency());;
        newWallet.setCreationDate(LocalDate.now());
        newWallet.setTransactions(null);
        newWallet.setArchived(false);
        newWallet.setInTransfers(null);
        newWallet.setOutTransfers(null);
        Wallet savedWallet = walletRepository.save(newWallet);
        return new WalletCreatedDTO(savedWallet);
    }

    public WalletDTO updateName(UpdateWalletDTO updatedName){
        Optional<Wallet> w = walletRepository.findById(updatedName.getId());
        if(w.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet updatedWallet = w.get();
        updatedWallet.setName(updatedName.getName());
        Wallet finishedWallet = walletRepository.save(updatedWallet);

        return new WalletDTO(finishedWallet);
    }

    public WalletDTO updateSavings(UpdateWalletDTO updateSavings){
        Optional<Wallet> w = walletRepository.findById(updateSavings.getId());
        if(w.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet updatedWallet = w.get();
        updatedWallet.setSavingsWallet(updateSavings.isSavings());
        Wallet finishedWallet = walletRepository.save(updatedWallet);

        return new WalletDTO(finishedWallet);
    }

    public WalletDTO updateArchived(UpdateWalletDTO updateArchived){
        Optional<Wallet> w = walletRepository.findById(updateArchived.getId());
        if(w.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet updatedWallet = w.get();
        updatedWallet.setArchived(updateArchived.isArchived());
        Wallet finishedWallet = walletRepository.save(updatedWallet);

        return new WalletDTO(finishedWallet);
    }

    public WalletDTO updateCurrency(UpdateWalletDTO updateCurrency){
        Optional<Wallet> w = walletRepository.findById(updateCurrency.getId());
        if(w.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet updatedWallet = w.get();
        updatedWallet.setCurrency(updateCurrency.getCurrency());
        Wallet finishedWallet = walletRepository.save(updatedWallet);

        return new WalletDTO(finishedWallet);
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

    public Wallet getWallet(int id){
        Optional<Wallet> pom = walletRepository.findById(id);
        if(pom.isEmpty()){
            throw new IllegalArgumentException("Novcanik ne postoji.");
        }
        Wallet newWallet = pom.get();
        return newWallet;
    }
}


