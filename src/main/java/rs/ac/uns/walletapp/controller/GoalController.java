package rs.ac.uns.walletapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.CreateGoalDTO;
import rs.ac.uns.walletapp.dto.GoalDTO;
import rs.ac.uns.walletapp.service.GoalService;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;
    public GoalController(GoalService goalService) { this.goalService = goalService; }

    @GetMapping("by-wallet")
    public ResponseEntity<GoalDTO> getGoalByWallet(@RequestParam int walletId) {
        GoalDTO dto = goalService.getGoalForWallet(walletId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("createGoal")
    public ResponseEntity<?> createGoal(@RequestBody CreateGoalDTO createGoalDTO) {
        try { return ResponseEntity.ok(goalService.createGoal(createGoalDTO)); }
        catch (RuntimeException e) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
    }
}

