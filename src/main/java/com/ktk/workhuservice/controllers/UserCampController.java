package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.data.UserCamp;
import com.ktk.workhuservice.mapper.UserCampMapper;
import com.ktk.workhuservice.service.CampService;
import com.ktk.workhuservice.service.SeasonService;
import com.ktk.workhuservice.service.UserCampService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class UserCampController {
    private UserCampService userCampService;
    private UserService userService;
    private UserCampMapper userCampMapper;
    private CampService campService;
    private SeasonService seasonService;

    public UserCampController(UserCampService userCampService, UserService userService, UserCampMapper userCampMapper, CampService campService, SeasonService seasonService) {
        this.userCampService = userCampService;
        this.userService = userService;
        this.userCampMapper = userCampMapper;
        this.campService = campService;
        this.seasonService = seasonService;
    }

    @GetMapping("/userCamps")
    public ResponseEntity getUserCamps(@Nullable @RequestParam("userId") Long userId,
                                       @Nullable @RequestParam("campId") Long campId,
                                       @Nullable @RequestParam("seasonYear") Integer seasonYear,
                                       @Nullable @RequestParam("participates") Boolean participates) {
        Optional<User> user = Optional.empty();
        Optional<Camp> camp = Optional.empty();
        Optional<Season> season = Optional.empty();
        if (userId != null) {
            user = userService.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("No user with id: " + userId);
            }
        }

        if (campId != null) {
            camp = campService.findById(campId);
            if (camp.isEmpty()) {
                return ResponseEntity.status(404).body("No camp with id: " + campId);
            }
        }

        if (seasonYear != null) {
            season = seasonService.findBySeasonYear(seasonYear);
            if (season.isEmpty()) {
                return ResponseEntity.status(404).body("No season with year: " + seasonYear);
            }
        }

        return ResponseEntity.status(200).body(userCampService.fetchByQuery(user.orElse(null), camp.orElse(null), season.orElse(null), participates).stream().map(userCampMapper::entityToDto));
    }

    @GetMapping("/userCamp")
    public ResponseEntity getUserCamp(@Nullable @RequestParam("userCampId") Long userCampId) {
        var userCamp = userCampService.findById(userCampId);
        if (userCamp.isEmpty()) {
            return ResponseEntity.status(404).body("No userCamp with id: " + userCampId);
        }
        return ResponseEntity.status(200).body(userCampMapper.entityToDto(userCamp.get()));
    }

    @GetMapping("/userCampByUser")
    public ResponseEntity getUserCampByUser(@Nullable @RequestParam("userId") Long userId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user with id: " + userId);
        }
        var userCamp = userCampService.findAllByUser(user.get());

        return ResponseEntity.status(200).body(userCamp.stream().map(userCampMapper::entityToDto));
    }

    @PostMapping("/userCamp")
    public ResponseEntity postUserCamp(@Valid @RequestBody UserCamp userCamp, @RequestParam Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user found with id: " + userId);
        }
        if (!user.get().isAdmin()) {
            return ResponseEntity.status(404).body("Unauthorized request");
        }
        return ResponseEntity.status(200).body(userCampMapper.entityToDto(userCampService.save(userCamp)));
    }

    @PutMapping("/userCamp")
    public ResponseEntity putUserCamp(@Valid @RequestBody UserCamp userCamp, @RequestParam("userCampId") Long userCampId) {
        if (userCampService.findById(userCampId).isEmpty() || !userCamp.getId().equals(userCampId)) {
            return ResponseEntity.status(400).body("Invalid userCampId");
        }
        return ResponseEntity.status(200).body(userCampMapper.entityToDto(userCampService.save(userCamp)));
    }
}
