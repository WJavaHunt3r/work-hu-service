package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceteam.PaceTeamService;
import com.ktk.workhuservice.data.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paceTeams")
public class PaceTeamController {

    private PaceTeamService paceTeamService;

    public PaceTeamController(PaceTeamService teamService, UserService userService) {
        this.paceTeamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {
        return ResponseEntity.status(200).body(paceTeamService.findAll());
    }
}
