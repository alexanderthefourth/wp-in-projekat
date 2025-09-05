package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
