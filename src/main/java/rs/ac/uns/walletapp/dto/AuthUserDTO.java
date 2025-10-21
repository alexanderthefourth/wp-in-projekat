package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Role;

@Getter @Setter
public class AuthUserDTO {
    private int id;
    private String username;
    private Role role;
    private boolean blocked;
}
