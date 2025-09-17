package rs.ac.uns.walletapp.services;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.uns.walletapp.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionService {

    public static Specification<Transaction> hasUser(Integer userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Transaction> hasCategory(Integer categoryId) {
        return (root, query, cb) ->
                categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Transaction> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            if (min != null && max != null) return cb.between(root.get("amount"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("amount"), min);
            return cb.lessThanOrEqualTo(root.get("amount"), max);
        };
    }

    public static Specification<Transaction> dateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();
            if (start != null && end != null) return cb.between(root.get("dateOfExecution"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("dateOfExecution"), start);
            return cb.lessThanOrEqualTo(root.get("dateOfExecution"), end);
        };
    }
}
