package rs.ac.uns.walletapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import rs.ac.uns.walletapp.dto.*;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.model.Type;
import rs.ac.uns.walletapp.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatsService {
  private static final BigDecimal ZERO = BigDecimal.ZERO;
    public enum Granularity { DAY, WEEK, MONTH, QUARTER, YEAR }

  @PersistenceContext
    private EntityManager em;

    private List<Transaction> load(int userId, LocalDate from, LocalDate to, Integer categoryId) {
        StringBuilder jpql = new StringBuilder(
            "select t from Transaction t where t.user.id = :userId"
        );
        if (from != null) {
            jpql.append(" and t.dateOfExecution >= :from");
        }
        if (to != null) {
            jpql.append(" and t.dateOfExecution <= :to");
        }
        if (categoryId != null) {
            jpql.append(" and t.category.id = :categoryId");
        }
        jpql.append(" order by t.dateOfExecution desc");

        TypedQuery<Transaction> q = em.createQuery(jpql.toString(), Transaction.class);
        q.setParameter("userId", userId);
        if (from != null) q.setParameter("from", from);
        if (to != null) q.setParameter("to", to);
        if (categoryId != null) q.setParameter("categoryId", categoryId);

        return q.getResultList();
    }

    private LocalDate bucketStart(LocalDate d, Granularity g) {
    switch (g) {
        case DAY:
            return d;
        case WEEK: {
            DayOfWeek first = WeekFields.ISO.getFirstDayOfWeek();
            int diff = (7 + (d.getDayOfWeek().getValue() - first.getValue())) % 7;
            return d.minusDays(diff);
        }
        case MONTH:
            return d.with(TemporalAdjusters.firstDayOfMonth());
        case QUARTER: {
            int qStartMonth = ((d.getMonthValue() - 1) / 3) * 3 + 1;
            return LocalDate.of(d.getYear(), qStartMonth, 1);
        }
        case YEAR:
            return d.with(TemporalAdjusters.firstDayOfYear());
        default:
            return d;
    }
}

private static BigDecimal safe(BigDecimal x) {
    return x == null ? ZERO : x;
}

private static String categoryNameOrFallback(Transaction t) {
    return t.getCategory() != null && t.getCategory().getName() != null
            ? t.getCategory().getName()
            : "Bez kategorije";
}

public List<SeriesPoint> seriesByPeriod(int userId,
                                        Granularity g,
                                        LocalDate from,
                                        LocalDate to,
                                        Integer categoryId) {
    List<Transaction> tx = load(userId, from, to, categoryId);
    if (tx.isEmpty()) return Collections.emptyList();

    Map<LocalDate, List<Transaction>> byBucket = tx.stream()
            .filter(t -> t.getDateOfExecution() != null)
            .collect(Collectors.groupingBy(t -> bucketStart(t.getDateOfExecution(), g)));

    List<SeriesPoint> points = byBucket.entrySet().stream()
            .map(e -> {
                LocalDate periodStart = e.getKey();
                BigDecimal income = e.getValue().stream()
                        .filter(t -> t.getType() == Type.INCOME)
                        .map(Transaction::getAmount)
                        .map(StatsService::safe)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal expense = e.getValue().stream()
                        .filter(t -> t.getType() == Type.EXPENSE)
                        .map(Transaction::getAmount)
                        .map(StatsService::safe)
                        .reduce(ZERO, BigDecimal::add);
                return new SeriesPoint(periodStart, income, expense);
            })
            .sorted(Comparator.comparing(SeriesPoint::getPeriodStart))
            .collect(Collectors.toList());

    return points;
}

public List<CategoryTotal> byCategory(int userId,
                                      Granularity g,
                                      LocalDate from,
                                      LocalDate to,
                                      Integer categoryId
) {
    List<Transaction> tx = load(userId, from, to, categoryId);
    if (tx.isEmpty()) return Collections.emptyList();

    Map<String, List<Transaction>> byCat = tx.stream()
            .collect(Collectors.groupingBy(StatsService::categoryNameOrFallback));

    List<CategoryTotal> rows = byCat.entrySet().stream()
            .map(e -> {
                String name = e.getKey();
                BigDecimal income = e.getValue().stream()
                        .filter(t -> t.getType() == Type.INCOME)
                        .map(Transaction::getAmount)
                        .map(StatsService::safe)
                        .reduce(ZERO, BigDecimal::add);
                BigDecimal expense = e.getValue().stream()
                        .filter(t -> t.getType() == Type.EXPENSE)
                        .map(Transaction::getAmount)
                        .map(StatsService::safe)
                        .reduce(ZERO, BigDecimal::add);
                return new CategoryTotal(name, income, expense);
            })
            .sorted(Comparator.comparing((CategoryTotal c) -> c.getExpense()).reversed())
            .collect(Collectors.toList());

    return rows;
}

public List<TxItem> topExpenses(int userId,
                                Granularity g,
                                LocalDate from,
                                LocalDate to,
                                int limit) {
    List<Transaction> tx = load(userId, from, to, null);
    if (tx.isEmpty()) return Collections.emptyList();

    return tx.stream()
            .filter(t -> t.getType() == Type.EXPENSE)
            .sorted((a, b) -> safe(b.getAmount()).compareTo(safe(a.getAmount())))
            .limit(Math.max(0, limit))
            .map(t -> {
                Integer walletId = t.getWallet() != null ? t.getWallet().getId() : null;
                return new TxItem(
                        t.getId(),
                        t.getName(),
                        safe(t.getAmount()),
                        t.getDateOfExecution(),
                        categoryNameOrFallback(t),
                        walletId
                );
            })
            .collect(Collectors.toList());
}
}
