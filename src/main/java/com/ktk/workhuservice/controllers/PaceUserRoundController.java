package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceuserround.PaceUserRound;
import com.ktk.workhuservice.data.paceuserround.PaceUserRoundService;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.dto.PaceUserRoundDto;
import com.ktk.workhuservice.mapper.PaceUserRoundMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paceUserRound")
public class PaceUserRoundController {

    private PaceUserRoundService paceUserRoundService;
    private RoundService roundService;
    private UserService userService;
    private PaceUserRoundMapper modelMapper;

    public PaceUserRoundController(PaceUserRoundService paceUserRoundService, RoundService roundService, UserService userService, PaceUserRoundMapper modelMapper) {
        this.paceUserRoundService = paceUserRoundService;
        this.roundService = roundService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity getPaceUserRounds(@Nullable @RequestParam("userId") Long userId,
                                            @Nullable @RequestParam("roundId") Long roundId,
                                            @Nullable @RequestParam("seasonYear") Integer seasonYear,
                                            @Nullable @RequestParam("paceTeamId") Long paceTeamId) {
        return ResponseEntity.status(200).body(paceUserRoundService.findByQuery(userId, roundId, seasonYear, paceTeamId).stream().map(this::entityToDto));
    }

    public PaceUserRoundDto entityToDto(PaceUserRound ur) {
        return modelMapper.entityToDto(ur);
    }
}
