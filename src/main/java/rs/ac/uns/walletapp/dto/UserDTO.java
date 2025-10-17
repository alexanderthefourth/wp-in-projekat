package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
}