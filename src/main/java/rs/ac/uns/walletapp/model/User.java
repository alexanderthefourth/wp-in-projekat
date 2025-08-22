package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;//unique
    private String pwHash;

    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Role role; //user, admin
    private String profilePicture;

    private String currency;
    private LocalDate regDate;
    private boolean blocked;

    public User(){}

    public User(String firstName, String lastName, String username, String email, String password, LocalDate birthDate, Role role, String currency) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.pwHash = password;
        this.birthDate = birthDate;
        this.role = role;
        this.currency = currency;
        this.regDate = LocalDate.now();
        this.blocked = false;
    }

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPwHash() {return pwHash;}
    public void setPwHash(String pwHash) {this.pwHash = pwHash;}

    public LocalDate getBirthDate() {return birthDate;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}

    public void setRole(Role role) {this.role = role;}
    public Role getRole() {return role;}

    public String getProfilePicture() {return profilePicture;}
    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}

    public String getCurrency() {return currency;}
    public void setCurrency(String currency) {this.currency = currency;}

    public LocalDate getRegDate() {return regDate;}

    public boolean isBlocked() {return blocked;}
    public void setBlocked(boolean blocked) {this.blocked = blocked;}
}
