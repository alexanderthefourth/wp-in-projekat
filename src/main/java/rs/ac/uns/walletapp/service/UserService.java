package rs.ac.uns.walletapp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.uns.walletapp.dto.AuthUserDTO;
import rs.ac.uns.walletapp.dto.LoginUserDTO;
import rs.ac.uns.walletapp.dto.RegisterUserDTO;
import rs.ac.uns.walletapp.dto.UserDTO;
import rs.ac.uns.walletapp.model.Role;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.repository.UserRepository;
import rs.ac.uns.walletapp.dto.AuthUserDTO;

import java.time.LocalDate;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User registerUser(RegisterUserDTO regUserDTO) {
        if(regUserDTO == null) {throw new IllegalArgumentException("null user sent.");}
        else if(regUserDTO.getEmail() == null || regUserDTO.getEmail().isEmpty()) { throw new IllegalArgumentException("empty email field.");}
        else if(regUserDTO.getUnhashedPassword() == null || regUserDTO.getUnhashedPassword().isEmpty()) { throw new IllegalArgumentException("empty password field.");}
        else if(regUserDTO.getUsername() == null || regUserDTO.getUsername().isEmpty()) { throw new IllegalArgumentException("empty username field.");}
        else if(regUserDTO.getFirstName() == null || regUserDTO.getFirstName().isEmpty()) { throw new IllegalArgumentException("empty firstName field.");}
        else if(regUserDTO.getLastName() == null || regUserDTO.getLastName().isEmpty()) { throw new IllegalArgumentException("empty name field.");}

        String hashedPassword = passwordEncoder.encode(regUserDTO.getUnhashedPassword());

        User user = new User();
        user.setFirstName(regUserDTO.getFirstName());
        user.setLastName(regUserDTO.getLastName());
        user.setUsername(regUserDTO.getUsername());
        user.setEmail(regUserDTO.getEmail());
        user.setPwHash(hashedPassword);
        user.setRole(Role.USER);
        user.setRegDate(LocalDate.now());

        return userRepository.save(user);
    }

    public boolean login(LoginUserDTO loginUserDTO) {
        User user = userRepository.findByUsername(loginUserDTO.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(loginUserDTO.getPassword(), user.getPwHash())) {
            return true;
        }
        return false;
    }

    public int getUserCount() {
        return (int) userRepository.count();
    }

    public UserDTO getUserProfile(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        return dto;
    }

    public UserDTO updateUserProfile(int userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setBirthDate(userDTO.getBirthDate());

        User updated = userRepository.save(user);

        UserDTO dto = new UserDTO();
        dto.setId(updated.getId());
        dto.setFirstName(updated.getFirstName());
        dto.setLastName(updated.getLastName());
        dto.setEmail(updated.getEmail());
        dto.setBirthDate(updated.getBirthDate());
        return dto;
    }

    public AuthUserDTO getAuthByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        AuthUserDTO dto = new AuthUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
