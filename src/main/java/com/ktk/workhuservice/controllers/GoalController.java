package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Goal;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.GoalDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.mapper.GoalMapper;
import com.ktk.workhuservice.service.GoalService;
import com.ktk.workhuservice.service.SeasonService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class GoalController {

    private GoalService goalService;
    private UserService userService;
    private SeasonService seasonService;
    private GoalMapper goalMapper;

    public GoalController(GoalService goalService, UserService userService, SeasonService seasonService, GoalMapper goalMapper) {
        this.goalService = goalService;
        this.userService = userService;
        this.seasonService = seasonService;
        this.goalMapper = goalMapper;
    }

    @GetMapping("/userSeasonGoal")
    public ResponseEntity getUserSeasonGoal(@RequestParam("userId") Long userId, @RequestParam("seasonYear") Integer seasonYear) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user with given id: " + userId);
        }
        Optional<Season> season = seasonService.findBySeasonYear(seasonYear);
        if (season.isEmpty()) {
            return ResponseEntity.status(404).body("No season with given id: " + seasonYear);
        }
        Optional<Goal> goal = goalService.findByUserAndSeason(user.get(), seasonYear);
        if (goal.isEmpty()) {
            return ResponseEntity.status(404).body("No goal found with userId: " + userId + " in season: " + seasonYear);
        }
        return ResponseEntity.status(200).body(goalMapper.entityToDto(goal.get()));
    }

    @GetMapping("/goal")
    public ResponseEntity getGoalById(@RequestParam("goalId") Long goalId) {
        Optional<Goal> goal = goalService.findById(goalId);
        if (goal.isEmpty()) {
            return ResponseEntity.status(404).body("No goal found with id: " + goal);
        }
        return ResponseEntity.status(200).body(goalMapper.entityToDto(goal.get()));
    }

    @GetMapping("/goals")
    public ResponseEntity getAllGoals(@Nullable @RequestParam("seasonYear") Integer seasonYear) {
        if (seasonYear == null) {
            return ResponseEntity.status(200).body(StreamSupport.stream(goalService.findAll().spliterator(), false).map(goalMapper::entityToDto));
        }
        Optional<Season> season = seasonService.findBySeasonYear(seasonYear);
        if (season.isEmpty()) {
            return ResponseEntity.status(404).body("No season with year: " + seasonYear);
        }
        List<Goal> goals = goalService.findBySeason(season.get());
        return ResponseEntity.status(200).body(goals.stream().map(goalMapper::entityToDto));
    }

    @PostMapping("/goal")
    public ResponseEntity saveGoal(@Valid @RequestBody GoalDto goalDto) {
        Optional<User> user = userService.findById(goalDto.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user found with id: " + goalDto.getUser().getId());
        }
        Goal goal = new Goal();
        goal.setUser(user.get());
        return ResponseEntity.status(200).body(goalMapper.entityToDto(goalService.save(goalMapper.dtoToEntity(goalDto, goal))));
    }

    @PutMapping("/goal")
    public ResponseEntity editGoal(@Valid @RequestBody GoalDto goalDto, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user found with id: " + userId);
        }
        if (userId.equals(goalDto.getUser().getId()) || user.get().getRole() == Role.ADMIN) {
            Optional<Goal> goal = goalService.findById(goalDto.getId());
            if (goal.isEmpty()) {
                return ResponseEntity.status(404).body("Goal not found with id: " + goalDto.getId());
            }
            return saveGoal(goalDto);
        }
        return ResponseEntity.status(404).body("User not allowed to change this goal");

    }

    @DeleteMapping("/goal")
    public ResponseEntity<?> deleteTransaction(@RequestParam("goalId") Long goalId, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied!");
        }
        if (goalService.existsById(goalId)) {
            goalService.deleteById(goalId);
            return ResponseEntity.status(200).body("Delete successful");
        }
        return ResponseEntity.status(403).body("No goal found with id:" + goalId);

    }

}
