package rs.ac.uns.walletapp.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.service.TransferService;

import java.util.List;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public Transfer createTransfer(@RequestBody Transfer transfer) {
        return transferService.createTransfer(transfer);
    }

    @GetMapping
    public List<Transfer> getAllTransfers() {
        return transferService.getAllTransfers();
    }

    @GetMapping("/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        return transferService.getTransferById(id);
    }
    /*
    @DeleteMapping("/{id}")
    public void deleteTransfer(@PathVariable int id) {
        transferService.deleteTransfer(id);
    }
    */
}
