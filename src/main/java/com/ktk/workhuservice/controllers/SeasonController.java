package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.service.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SeasonController {
    private SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/seasons")
    public ResponseEntity getSeasons(){
        return ResponseEntity.status(200).body(seasonService.findAll());
    }
}
