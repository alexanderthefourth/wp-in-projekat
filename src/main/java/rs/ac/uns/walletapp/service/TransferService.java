package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.Transfer;
import rs.ac.uns.walletapp.repository.TransferRepository;

import java.util.List;

@Service
public class TransferService {
    private final TransferRepository transferRepository;

    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public Transfer createTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public Transfer getTransferById(int id) {
        return transferRepository.findById(id).orElse(null);
    }
    public void deleteTransfer(int id) {
        transferRepository.deleteById(id);
    }
}
