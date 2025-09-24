package rs.ac.uns.walletapp.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.dto.UserDTO;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
