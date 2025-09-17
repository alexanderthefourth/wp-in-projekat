package rs.ac.uns.walletapp.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.walletapp.dto.WalletCreatedDTO;
import rs.ac.uns.walletapp.dto.WalletDTO;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.services.WalletService;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("walletCreate")
    public ResponseEntity<WalletCreatedDTO> createWallet(@RequestBody WalletDTO walletDTO){
        try{
            WalletCreatedDTO walletCR = walletService.createWallet(walletDTO);
            return new ResponseEntity<>(walletCR, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable int id) {
        try {
            WalletDTO wallet = walletService.getWallet(id);
            if (wallet != null) {
                return new ResponseEntity<>(wallet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

    @PutMapping("{id}/nameUpdate")
    public ResponseEntity<WalletDTO> updateName(@PathVariable int id, @RequestParam String name) {
        try {
            WalletDTO walletDTO = walletService.updateName(id, name);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}/savingsUpdate")
    public ResponseEntity<WalletDTO> updateSavings(@PathVariable int id, @RequestParam boolean savings) {
        try {
            WalletDTO walletDTO = walletService.updateSavings(id, savings);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}/archivedUpdate")
    public ResponseEntity<WalletDTO> updateArchived(@PathVariable int id, @RequestParam boolean archived) {
        try {
            WalletDTO walletDTO = walletService.updateArchived(id, archived);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}/currencyUpdate")
    public ResponseEntity<WalletDTO> updateCurrency(@PathVariable int id, @RequestParam String currencyName) {
        try {
            WalletDTO walletDTO = walletService.updateCurrency(id, currencyName);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}/viewCurrBal")
    public ResponseEntity<BigDecimal> viewCurrBalance(@PathVariable("id") int id){
        try{
            BigDecimal currBal = walletService.viewCurrBal(id);
            return new ResponseEntity<>(currBal, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}/viewTransfers")
    public ResponseEntity<List<Transfer>> viewAllTransfers(@PathVariable("id") int id){
        try{
            List<Transfer> tr = walletService.viewTransfers(id);
            return new ResponseEntity<>(tr, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
