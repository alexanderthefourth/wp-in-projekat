package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Entity
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String pwHash;

    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Role role; //user, admin
    private String profilePicture;

    private String currency;
    private LocalDate regDate;
    private boolean blocked;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Wallet> walletList;

    public User(){}

}
