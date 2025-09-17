package rs.ac.uns.walletapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.dto.GoalDTO;
import rs.ac.uns.walletapp.service.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public List<GoalDTO> getUserGoals(@RequestParam int userId) {
        return goalService.getGoalsForUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> getGoalById(@PathVariable int id, @RequestParam int userId) {
        GoalDTO goal = goalService.getGoalById(id, userId);
        if (goal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    public GoalDTO createGoal(@RequestParam int userId, @RequestBody GoalDTO goalDTO) {
        return goalService.createGoal(userId, goalDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable int id, @RequestParam int userId) {
        boolean deleted = goalService.deleteGoal(id, userId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
