package rs.ac.uns.walletapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Type;

@Getter
@Setter
public class CreateTransactionDTO {
    private String name;
    private BigDecimal amount;
    private Type type;
    private LocalDate dateOfExecution;
    private boolean repeatable;
    private boolean activeRepeat;
    private String frequency;
    private int walletId;

    public CreateTransactionDTO(){}
}
