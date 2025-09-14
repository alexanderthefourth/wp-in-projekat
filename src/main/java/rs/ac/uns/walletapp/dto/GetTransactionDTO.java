package rs.ac.uns.walletapp.dto;

import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.model.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTransactionDTO {
    private int id;
    private String name;
    private BigDecimal amount;
    private Type type;
    private LocalDate dateOfExecution;

    public GetTransactionDTO(Transaction t){
        this.id = t.getId();
        this.name = t.getName();
        this.amount = t.getAmount();
        this.type = t.getType();
        this.dateOfExecution = t.getDateOfExecution();
    }
}
