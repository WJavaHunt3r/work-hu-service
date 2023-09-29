package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.TeamColor;
import com.ktk.workhuservice.service.TeamService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getUser(@RequestParam String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.isPresent() ?
                ResponseEntity.status(200).body(entityToDto(user.get())) : ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAll().spliterator(), false).map((Function<User, Object>) this::entityToDto));
    }

    @GetMapping("/usersByTeam")
    public ResponseEntity<?> getAllTeamMembersByColor(@RequestParam TeamColor color) {
        Optional<Team> team = teamService.findByColor(color);
        if (team.isPresent()) {
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAllByTeam(team.get()).spliterator(), false).map((Function<User, Object>) this::entityToDto));
        } else {
            return ResponseEntity.status(404).body("Team Color not found");
        }
    }

//    @GetMapping("/user")
//    public ResponseEntity<?> getTransactions(@RequestParam Long myShareId) {
//        return ResponseEntity.status(200).body(userService.findByMyShareId(myShareId).map(this::entityToDto));
//    }

    private UserDto entityToDto(User u) {
        UserDto dto = new UserDto();
        dto.setTeam(u.getTeam());
        dto.setFirstname(u.getFirstname());
        dto.setBaseMyShareCredit(u.getBaseMyShareCredit());
        dto.setBirthDate(u.getBirthDate());
        dto.setCurrentMyShareCredit(u.getCurrentMyShareCredit());
        dto.setGoal(u.getGoal());
        dto.setLastname(u.getLastname());
        dto.setPoints(u.getPoints());
        dto.setRole(u.getRole());
        dto.setMyShareID(u.getMyShareID());
        dto.setId(u.getId());
        return dto;
    }

}
