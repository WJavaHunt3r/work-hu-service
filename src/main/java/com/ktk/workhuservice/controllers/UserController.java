package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.service.TeamService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    private TeamService teamService;

    public UserController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@Nullable @RequestParam("username") String username, @Nullable @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            userService.calculateUserPoints(user.get());
            return ResponseEntity.status(200).body(userService.entityToDto(user.get()));
        }
        else return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@Nullable @RequestParam("teamId") Long teamId, @Nullable @RequestParam("listO36") Boolean listO36) {
        if (teamId != null) {
            Optional<Team> team = teamService.findById(teamId);
            if (team.isEmpty()) {
                return ResponseEntity.status(400).body("No team found with id: " + teamId);
            }
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAllByTeam(team.get()).spliterator(), false).map((Function<User, Object>) userService::entityToDto));
        }
        if (listO36 == null || !listO36) {
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAllYouth().spliterator(), false).map((Function<User, Object>) userService::entityToDto));
        }
        return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAll().spliterator(), false).map((Function<User, Object>) userService::entityToDto));
    }

//    @GetMapping("/user")
//    public ResponseEntity<?> getTransactions(@RequestParam Long myShareId) {
//        return ResponseEntity.status(200).body(userService.findByMyShareId(myShareId).map(this::entityToDto));
//    }

}
