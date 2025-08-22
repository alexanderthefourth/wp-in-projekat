package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    private boolean predefined;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Category() {}
    public Category(String name, Type type, boolean predefined, User user) {
        this.name = name;
        this.type = type;
        this.predefined = predefined;
        this.user = user;
        if(predefined) this.user = null;

    }
}
