package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceuserround.PaceUserRound;
import com.ktk.workhuservice.data.paceuserround.PaceUserRoundService;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.dto.PaceUserRoundDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/pacePaceUserRound")
public class PaceUserRoundController {

    private PaceUserRoundService paceUserRoundService;
    private RoundService roundService;
    private UserService userService;
    private ModelMapper modelMapper;

    public PaceUserRoundController(PaceUserRoundService paceUserRoundService, RoundService roundService, UserService userService, ModelMapper modelMapper) {
        this.paceUserRoundService = paceUserRoundService;
        this.roundService = roundService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity getPaceUserRounds(@Nullable @RequestParam("userId") Long userId,
                                            @Nullable @RequestParam("roundId") Long roundId,
                                            @Nullable @RequestParam("seasonYear") Integer seasonYear) {
        if (userId != null) {
            var user = userService.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("No user with given id: " + userId);
            }
            if (roundId != null) {
                Optional<Round> round = roundService.findById(roundId);
                if (round.isEmpty()) {
                    return ResponseEntity.status(404).body("No round with given id: " + roundId);
                }
                Optional<PaceUserRound> pur = paceUserRoundService.findByUserAndRound(user.get(), round.get());
                if (pur.isPresent()) {
                    return ResponseEntity.status(200).body(entityToDto(pur.get()));
                } else {
                    return ResponseEntity.status(404).body("User not in BUK");
                }
            }
            if (seasonYear != null) {
                return ResponseEntity.status(200).body(paceUserRoundService.findByUserAndSeasonYear(user.get(), seasonYear).stream().map(this::entityToDto));
            }
            return ResponseEntity.status(200).body(paceUserRoundService.findByUser(user.get()).stream().map(this::entityToDto));

        }
        return ResponseEntity.status(200).body(StreamSupport.stream(paceUserRoundService.findAll().spliterator(), false).map(this::entityToDto));
    }

    public PaceUserRoundDto entityToDto(PaceUserRound ur) {
        return modelMapper.map(ur, PaceUserRoundDto.class);
    }
}
