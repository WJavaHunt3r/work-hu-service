package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.enums.TeamColor;
import com.ktk.workhuservice.service.TeamService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    private TeamService teamService;

    public UserController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getTransactions() {
        return ResponseEntity.status(200).body(userService.getAll());
    }

    @GetMapping("/usersByTeam")
    public ResponseEntity<?> getAllTeamMembersByColor(@RequestParam TeamColor color){
        Optional<Team> team = teamService.findByColor(color);
        if (team.isPresent()){
            return ResponseEntity.status(200).body(userService.findAllByTeam(team.get()));
        } else {
            return ResponseEntity.status(404).body("Team Color not found");
        }

    }

}
