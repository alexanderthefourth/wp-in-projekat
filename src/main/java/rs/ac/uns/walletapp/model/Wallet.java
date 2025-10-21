package rs.ac.uns.walletapp.model;

import jakarta.annotation.Nullable;
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
    @Column(precision = 19, scale = 4)
    private BigDecimal initBal;

    @Column(precision = 19, scale = 4)
    private BigDecimal currBal;

    @ManyToOne
    @JoinColumn(name = "curr_name")
    private Currency currency;
    private LocalDate creationDate;
    private boolean archived;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id")
    private List<Transaction> transactions;

    private boolean savingsWallet;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    @Nullable
    private Goal goal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<Transfer> outTransfers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<Transfer> inTransfers;


    public Wallet(){}

    public Wallet(String name, BigDecimal initBal, Currency currency, boolean savingsWallet){
        this.name = name;
        this.initBal = initBal;
        this.currency = currency;
        this.savingsWallet = savingsWallet;
    }

}
