package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceteamround.PaceTeamRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paceTeamRounds")
public class PaceTeamRoundController {

    private PaceTeamRoundService service;

    public PaceTeamRoundController(PaceTeamRoundService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity getAllPaceTeamRounds(@RequestParam("seasonYear") int seasonYear) {
        return ResponseEntity.status(200).body(service.findAll(seasonYear));
    }
}
