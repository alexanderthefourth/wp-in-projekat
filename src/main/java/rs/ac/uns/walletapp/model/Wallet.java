package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

public class Wallet implements Serializable {
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean savingsWallet;
    private boolean archived;

    public Wallet(){}

    public Wallet(String name, String initBal, String currBal, Currency currency, User user, boolean savingsWallet, boolean archived) {
        this.name = name;
        this.initBal = initBal;
        this.currBal = currBal;
        this.currency = currency;
        this.creationDate = LocalDate.now();
        this.user = user;
        this.savingsWallet = savingsWallet;
        this.archived = archived;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getInitBal() {return initBal;}
    public void setInitBal(String initBal) {this.initBal = initBal;}

    public String getCurrBal() {return currBal;}
    public void setCurrBal(String currBal) {this.currBal = currBal;}

    public Currency getCurrency() {return currency;}
    public void setCurrency(Currency currency) {this.currency = currency;}

    public LocalDate getCreationDate() {return creationDate;}
    public void setCreationDate(LocalDate creationDate) {this.creationDate = creationDate;}

    public User getUser() {return user;}

    public boolean isSavingsWallet() {return savingsWallet;}
    public void setSavingsWallet(boolean savingsWallet) {this.savingsWallet = savingsWallet;}

    public boolean isArchived() {return archived;}
    public void setArchived(boolean archived) {this.archived = archived;}
}
