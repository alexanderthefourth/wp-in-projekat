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
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.walletapp.dto.UpdateWalletDTO;
import rs.ac.uns.walletapp.dto.WalletCreatedDTO;
import rs.ac.uns.walletapp.dto.WalletDTO;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.services.WalletService;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("/walletCreate")
    public ResponseEntity<WalletCreatedDTO> createWallet(@RequestBody WalletDTO walletDTO){
        try{
            WalletCreatedDTO walletCR = walletService.createWallet(walletDTO);
            return new ResponseEntity<>(walletCR, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/delete")
    public void deleteWallet(){
         
    }

    @PutMapping("/{id}/nameUpdate")
    public ResponseEntity<WalletDTO> updateName(@RequestBody UpdateWalletDTO updateWalletDTO){
        try{
            WalletDTO walletDTO = walletService.updateName(updateWalletDTO);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/savingsUpdate")
    public ResponseEntity<WalletDTO> updateSavings(@RequestBody UpdateWalletDTO updateWalletDTO){
        try{
            WalletDTO walletDTO = walletService.updateSavings(updateWalletDTO);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/archivedUpdate")
    public ResponseEntity<WalletDTO> updateArchived(@RequestBody UpdateWalletDTO updateWalletDTO){
        try{
            WalletDTO walletDTO = walletService.updateArchived(updateWalletDTO);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/currencyUpdate")
    public ResponseEntity<WalletDTO> updateCurrency(@RequestBody UpdateWalletDTO updateWalletDTO){
        try{
            WalletDTO walletDTO = walletService.updateCurrency(updateWalletDTO);
            return new ResponseEntity<>(walletDTO, HttpStatus.OK);
        }catch(Exception e){
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

    @GetMapping("/{id}/viewTransfers")
    public ResponseEntity<List<Transfer>> viewAllTransfers(@PathVariable("id") int id){
        try{
            List<Transfer> tr = walletService.viewTransfers(id);
            return new ResponseEntity<>(tr, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
