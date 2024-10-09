package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.paceteam.PaceTeamService;
import com.ktk.workhuservice.data.paceteamround.PaceTeamRoundService;
import com.ktk.workhuservice.data.seasons.SeasonService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;
    private PaceTeamService paceTeamService;
    private UserMapper userMapper;
    private SeasonService seasonService;
    private PaceTeamRoundService paceTeamRoundService;

    public UserController(UserService userService, PaceTeamService paceTeamService, UserMapper modelMapper, SeasonService seasonService, PaceTeamRoundService paceTeamRoundService) {
        this.userService = userService;
        this.paceTeamService = paceTeamService;
        this.userMapper = modelMapper;
        this.seasonService = seasonService;
        this.paceTeamRoundService = paceTeamRoundService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> userById = userService.findById(id);
        if (userById.isPresent()) {
            return ResponseEntity.status(200).body(userMapper.entityToDto(userById.get()));
        }

        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/myShare/{myShareId}")
    public ResponseEntity<?> getUserByMYShare(@PathVariable Long myShareId) {
        Optional<User> userByMyShareId = userService.findByMyShareId(myShareId);
        if (userByMyShareId.isPresent()) {
            return ResponseEntity.status(200).body(userMapper.entityToDto(userByMyShareId.get()));
        }

        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userByMyShareId = userService.findByUsername(username);
        if (userByMyShareId.isPresent()) {
            return ResponseEntity.status(200).body(userMapper.entityToDto(userByMyShareId.get()));
        }

        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping
    public ResponseEntity<?> getUsers(@Nullable @RequestParam("teamId") Long teamId, @Nullable @RequestParam("listO36") Boolean listO36) {
        if (teamId != null) {
            Optional<PaceTeam> team = paceTeamService.findById(teamId);
            if (team.isEmpty()) {
                return ResponseEntity.status(400).body("No team found with id: " + teamId);
            }
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAllByPaceTeam(team.get(), seasonService.findBySeasonYear(2024).get()).spliterator(), false).map((Function<User, Object>) userMapper::entityToDto));
        }
        if (listO36 == null || !listO36) {
            return ResponseEntity.status(200).body(StreamSupport.stream(userService.getYouth().spliterator(), false).map((Function<User, Object>) userMapper::entityToDto));
        }
        return ResponseEntity.status(200).body(StreamSupport.stream(userService.findAll().spliterator(), false).map(userMapper::entityToDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id, @RequestParam("modifyUserId") Long modifyUserId) {
        Optional<User> createUser = userService.findById(modifyUserId);
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + modifyUserId);
        }
        if (createUser.get().getRole().equals(Role.USER) && !userDto.getId().equals(id)) {
            return ResponseEntity.status(403).body("Permission denied:");
        }
        Optional<User> user = userService.findById(userDto.getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userDto.getId());
        }
        return ResponseEntity.status(200).body(userMapper.entityToDto(userService.save(userMapper.dtoToEntity(userDto, user.get()))));
    }

    @GetMapping("/setPaceTeams")
    public ResponseEntity<?> setPaceTeams() {
        userService.setPaceTeams(createBUKTeam(), createSamvirkTeam());
        paceTeamRoundService.createTeamRounds();
        return ResponseEntity.status(200).body("Pace Teams set");
    }

    private PaceTeam createSamvirkTeam() {
        Optional<PaceTeam> team = paceTeamService.findByTeamName("Team Samvirk");
        if (team.isEmpty()) {
            PaceTeam samvirk = new PaceTeam();
            samvirk.setTeamLeaderId(120L);
            samvirk.setCoins(0);
            samvirk.setTeamName("Team Samvirk");
            return paceTeamService.save(samvirk);
        }
        return team.get();
    }

    private PaceTeam createBUKTeam() {
        Optional<PaceTeam> team = paceTeamService.findByTeamName("Team BUK");
        if (team.isEmpty()) {
            PaceTeam buk = new PaceTeam();
            buk.setTeamLeaderId(134L);
            buk.setCoins(0);
            buk.setTeamName("Team BUK");
            return paceTeamService.save(buk);
        }
        return team.get();
    }
}
