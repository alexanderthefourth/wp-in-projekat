package rs.ac.uns.walletapp.repository;

import rs.ac.uns.walletapp.interfaces.ShowWalletInterface;
import rs.ac.uns.walletapp.model.Wallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    @Modifying
    @Query(value = "UPDATE wallet SET user_id = :userId WHERE id = :walletId", nativeQuery = true)
    int attachToUser(@Param("walletId") int walletId, @Param("userId") int userId);

    @Query(value = """
        SELECT w.id   AS id,
               w.name AS name,
               w.curr_name AS currencyName
        FROM wallet w
        WHERE w.user_id = :userId
        ORDER BY w.name
    """, nativeQuery = true)
    List<ShowWalletInterface> findSimpleByUser(@Param("userId") int userId);
    
}
