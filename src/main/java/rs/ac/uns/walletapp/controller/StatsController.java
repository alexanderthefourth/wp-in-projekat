package rs.ac.uns.walletapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.*;
import rs.ac.uns.walletapp.service.StatsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService stats;

    @GetMapping("/series")
    public List<SeriesPoint> series(
            @RequestParam int userId,
            @RequestParam StatsService.Granularity granularity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Integer categoryId
    ) {
        return stats.seriesByPeriod(userId, granularity, from, to, categoryId);
    }

    @GetMapping("/by-category")
    public List<CategoryTotal> byCategory(
            @RequestParam int userId,
            @RequestParam StatsService.Granularity granularity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return stats.byCategory(userId, granularity, from, to, null);
    }

    @GetMapping("/top-expenses")
    public List<TxItem> topExpenses(
            @RequestParam int userId,
            @RequestParam StatsService.Granularity granularity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return stats.topExpenses(userId, granularity, from, to, limit);
    }
}
