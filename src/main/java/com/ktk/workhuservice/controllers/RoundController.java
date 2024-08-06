package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class RoundController {
    private RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/rounds")
    public ResponseEntity getRounds(@Nullable @RequestParam("seasonYear") Integer seasonYear, @Nullable @RequestParam("activeRounds") Boolean activeRounds) {
        if (seasonYear != null) {
            return ResponseEntity.status(200).body(roundService.findAllByRoundYear(seasonYear));
        }
        return ResponseEntity.status(200).body(roundService.findAll());
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

    @PostMapping("/round")
    public ResponseEntity postRound(@Valid @RequestBody Round round) {
        return ResponseEntity.status(200).body(roundService.save(round));
    }

    @PutMapping("/round")
    public ResponseEntity putRound(@Valid @RequestBody Round round, @RequestParam("roundId") Long roundId) {
        if (roundService.findById(roundId).isEmpty() || !round.getId().equals(roundId)) {
            return ResponseEntity.status(400).body("Invalid roundId");
        }
        return ResponseEntity.status(200).body(roundService.save(round));
    }

    @GetMapping("/currentRound")
    public ResponseEntity getCurrentRound() {
        return ResponseEntity.status(200).body(roundService.findRoundByDate(LocalDateTime.now()));
    }
}
