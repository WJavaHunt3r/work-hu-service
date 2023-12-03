package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.service.TeamService;
import com.ktk.workhuservice.service.UserService;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    public UserController(UserService userService, TeamService teamService, ModelMapper modelMapper) {
        this.userService = userService;
        this.teamService = teamService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@Nullable @RequestParam("username") String username, @Nullable @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            userService.calculateUserPoints(user.get());
            return ResponseEntity.status(200).body(entityToDto(user.get()));
        } else if (userId != null) {
            Optional<User> userById = userService.findById(userId);
            if (userById.isPresent()) {
                return ResponseEntity.status(200).body(entityToDto(userById.get()));
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
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAllByTeam(team.get()).spliterator(), false).map((Function<User, Object>) this::entityToDto));
        }
        if (listO36 == null || !listO36) {
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAllYouth().spliterator(), false).map((Function<User, Object>) this::entityToDto));
        }
        return ResponseEntity.status(200).body(StreamSupport.stream(userService.getAll().spliterator(), false).map(this::entityToDto));
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
        User updatedUser = dtoToEntity(userDto, user.get());
        return ResponseEntity.status(200).body(entityToDto(userService.save(updatedUser)));
    }

    private UserDto entityToDto(User u) {
        return modelMapper.map(u, UserDto.class);
    }

    private User dtoToEntity(UserDto dto, User user){
        user.setRole(dto.getRole());
        user.setGoal(dto.getGoal());
        user.setTeam(dto.getTeam());
        user.setBirthDate(dto.getBirthDate());
        user.setLastname(dto.getLastname());
        user.setFirstname(dto.getFirstname());
        user.setBaseMyShareCredit(dto.getBaseMyShareCredit());
        user.setGoal(dto.getGoal());
        return user;
    }

}
