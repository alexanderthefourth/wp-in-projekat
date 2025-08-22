package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private double amount;
    private Type type;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
    private boolean repeatable;
    private String frequency;
    private Wallet wallet;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {}

    public Transaction(String name, double amount, Type type, Category category, boolean repeatable, String frequency, Wallet wallet, User user) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.repeatable = repeatable;
        this.frequency = frequency;
        this.wallet = wallet;
        this.user = user;
    }
}
