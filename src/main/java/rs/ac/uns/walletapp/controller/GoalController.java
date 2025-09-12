package rs.ac.uns.walletapp.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.walletapp.model.Goal;
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
    public List<Goal> getAllGoals() {
        return goalService.getAllGoals();
    }

    @GetMapping("/{id}")
    public Goal getGoalById(@PathVariable int id) {
        return goalService.getGoalById(id);
    }

    @PostMapping
    public Goal createGoal(@RequestBody Goal goal) {
        return goalService.createGoal(goal);
    }

    @PutMapping("/{id}")
    public Goal updateGoal(@PathVariable int id, @RequestBody Goal goalInfo) {
        return goalService.updateGoal(id, goalInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable int id) {
        goalService.deleteGoal(id);
    }
}
