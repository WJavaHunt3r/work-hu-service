package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.service.RoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class RoundController {
    private RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/rounds")
    public ResponseEntity getRounds() {
        return ResponseEntity.status(200).body(roundService.getAll());
    }

    @GetMapping("/round")
    public ResponseEntity getRound(@Nullable @RequestParam("roundId") Long roundId) {
        if (roundId != null) {
            var round = roundService.findById(roundId);
            if (round.isEmpty()) {
                return ResponseEntity.status(404).body("No round with id: " + roundId);
            }
            return ResponseEntity.status(200).body(round.get());
        }
        return ResponseEntity.status(200).body(roundService.findRoundByDate(LocalDateTime.now()));
    }
}
