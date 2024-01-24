package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.mapper.UserMapper;
import com.ktk.workhuservice.service.SeasonService;
import com.ktk.workhuservice.service.TeamService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    private TeamService teamService;
    private UserMapper userMapper;
    private SeasonService seasonService;

    public UserController(UserService userService, TeamService teamService, UserMapper modelMapper, SeasonService seasonService) {
        this.userService = userService;
        this.teamService = teamService;
        this.userMapper = modelMapper;
        this.seasonService = seasonService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@Nullable @RequestParam("username") String username, @Nullable @RequestParam("userId") Long userId, @Nullable @RequestParam("myShareId") Long myShareId) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            userService.calculateUserPoints(user.get());
            return ResponseEntity.status(200).body(userMapper.entityToDto(user.get()));
        } else if (userId != null) {
            Optional<User> userById = userService.findById(userId);
            if (userById.isPresent()) {
                return ResponseEntity.status(200).body(userMapper.entityToDto(userById.get()));
            }
        }
        if (myShareId != null) {
            Optional<User> userByMyShareId = userService.findByMyShareId(myShareId);
            if (userByMyShareId.isPresent()) {
                return ResponseEntity.status(200).body(userMapper.entityToDto(userByMyShareId.get()));
            }
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@Nullable @RequestParam("teamId") Long teamId, @Nullable @RequestParam("listO36") Boolean listO36) {
        if (teamId != null) {
            Optional<Team> team = teamService.findById(teamId);
            if (team.isEmpty()) {
                return ResponseEntity.status(400).body("No team found with id: " + teamId);
            }
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAllByTeam(team.get(), seasonService.findBySeasonYear(2024).get()).spliterator(), false).map((Function<User, Object>) userMapper::entityToDto));
        }
        if (listO36 == null || !listO36) {
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAllYouth().spliterator(), false).map((Function<User, Object>) userMapper::entityToDto));
        }
        return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAll().spliterator(), false).map(userMapper::entityToDto));
    }

    @PutMapping("/user")
    public ResponseEntity<?> putUser(@Valid @RequestBody UserDto userDto, @RequestParam("userId") Long userId) {
        Optional<User> createUser = userService.findById(userId);
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (createUser.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied:");
        }
        Optional<User> user = userService.findById(userDto.getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userDto.getId());
        }
        return ResponseEntity.status(200).body(userMapper.entityToDto(userService.save(userMapper.dtoToEntity(userDto, user.get()))));
    }

}
