package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Wallet{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String initBal;
    private String currBal;

    @ManyToOne
    @JoinColumn(name = "curr_name")
    private Currency currency;
    private LocalDate creationDate;

    private boolean savingsWallet;
    private boolean archived;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id")
    private List<Transaction> transactions;
    public Wallet(){}

}
