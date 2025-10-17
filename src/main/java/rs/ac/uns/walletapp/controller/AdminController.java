package rs.ac.uns.walletapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.DashboardDto;
import rs.ac.uns.walletapp.model.Transaction;
import rs.ac.uns.walletapp.service.AdminService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("transactions")
    public List<Transaction> getTransactions(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "dateOfExecution") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return adminService.getFilteredTransactions(userId, categoryId, minAmount, maxAmount, startDate, endDate, sortBy, sortDir);
    }

    @GetMapping("dashboard")
    public DashboardDto getDashboard() {
        DashboardDto dto = new DashboardDto();
        dto.setTotalUsers(adminService.getTotalUsers());
        dto.setActiveUsers(adminService.getActiveUsers());
        dto.setAverageMoneyActiveUsers(adminService.getAvgMoneyOfActiveUsers());
        dto.setTotalMoneyInSystem(adminService.getTotalMoneyInSystem());
        dto.setTop10Last30Days(adminService.getTop10Last30Days());
        dto.setTop10Today(adminService.getTop10Today());
        return dto;
    }
}
