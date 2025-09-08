package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private BigDecimal initBal;
    private BigDecimal currBal;

    @ManyToOne
    @JoinColumn(name = "curr_name")
    private Currency currency;
    private LocalDate creationDate;

    private boolean savingsWallet;
    private boolean archived;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id")
    private List<Transaction> transactions;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<Goal> goals;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<Transfer> outTransfers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<Transfer> inTransfers;

    public Wallet(){}

}
