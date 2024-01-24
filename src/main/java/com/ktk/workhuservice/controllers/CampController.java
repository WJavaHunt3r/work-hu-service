package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.service.CampService;
import com.ktk.workhuservice.service.SeasonService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class CampController {

    private CampService campService;
    private UserService userService;
    private SeasonService seasonService;

    public CampController(CampService campService, UserService userService, SeasonService seasonService) {
        this.campService = campService;
        this.userService = userService;
        this.seasonService = seasonService;
    }

    @GetMapping("/camps")
    public ResponseEntity getCamps(@Nullable @RequestParam("seasonId") Long seasonId) {
        if (seasonId == null) {
            return ResponseEntity.status(200).body(campService.findAll());
        }
        Optional<Season> season = seasonService.findById(seasonId);
        if (season.isEmpty()) {
            return ResponseEntity.status(404).body("No season with given id: " + seasonId);
        }
        List<Camp> goals = campService.findAllBySeason(season.get());
        return ResponseEntity.status(200).body(goals);
    }

    @GetMapping("/camp")
    public ResponseEntity getCamp(@Nullable @RequestParam("campId") Long campId) {
        var camp = campService.findById(campId);
        if (camp.isEmpty()) {
            return ResponseEntity.status(404).body("No camp with id: " + campId);
        }
        return ResponseEntity.status(200).body(camp.get());
    }

    @PostMapping("/camp")
    public ResponseEntity postCamp(@Valid @RequestBody Camp camp, @RequestParam Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user found with id: " + userId);
        }
        if (!user.get().isAdmin()) {
            return ResponseEntity.status(404).body("Unauthorized request");
        }
        return ResponseEntity.status(200).body(campService.save(camp));
    }

    @PutMapping("/camp")
    public ResponseEntity putCamp(@Valid @RequestBody Camp camp, @RequestParam("campId") Long campId) {
        if (campService.findById(campId).isEmpty() || !camp.getId().equals(campId)) {
            return ResponseEntity.status(400).body("Invalid campId");
        }
        return ResponseEntity.status(200).body(campService.save(camp));
    }
}
