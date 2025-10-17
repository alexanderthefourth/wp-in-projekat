package rs.ac.uns.walletapp.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String unhashedPassword;
}
