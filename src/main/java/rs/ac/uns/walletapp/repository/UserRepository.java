package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
}
