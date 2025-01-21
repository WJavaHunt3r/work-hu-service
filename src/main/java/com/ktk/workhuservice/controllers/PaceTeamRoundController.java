package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceteamround.PaceTeamRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paceTeamRounds")
public class PaceTeamRoundController {

    private PaceTeamRoundService service;

    public PaceTeamRoundController(PaceTeamRoundService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity getAllPaceTeamRounds(@RequestParam("seasonYear") int seasonYear) {
        return ResponseEntity.status(200).body(service.findAll(seasonYear));
    }

    @PostMapping("/recalculate")
    public ResponseEntity recalculateTeamRoundsScore() {
        service.calculateAllTeamRoundPoints();
        return ResponseEntity.status(200).body("Recalculation successful");
    }

}
