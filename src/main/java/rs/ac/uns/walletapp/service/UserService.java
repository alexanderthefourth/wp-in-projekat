package rs.ac.uns.walletapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.walletapp.model.User;
import rs.ac.uns.walletapp.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {return userRepository.save(user);}

    public List<User> getAllUsers() {return userRepository.findAll();}

    public User getUserById(int id) {return userRepository.findById(id).orElse(null);}

    public void updateUser(int id, User updatedUser) {
        User currentUser = userRepository.findById(id).orElse(null);

        if (currentUser == null) {
            return;
        }

        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setPwHash(updatedUser.getPwHash());
        currentUser.setCurrency(updatedUser.getCurrency());

        userRepository.save(currentUser);
    }

    public boolean deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}


