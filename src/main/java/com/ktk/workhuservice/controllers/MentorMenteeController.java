package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.MentorMentee;
import com.ktk.workhuservice.dto.MentorMenteeDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.mapper.MentorMenteeMapper;
import com.ktk.workhuservice.service.MentorMenteeService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class MentorMenteeController {

    private MentorMenteeService mentorMenteeService;
    private MentorMenteeMapper mapper;
    private UserService userService;

    public MentorMenteeController(MentorMenteeService mentorMenteeService, MentorMenteeMapper mapper, UserService userService) {
        this.mentorMenteeService = mentorMenteeService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/mentees")
    public ResponseEntity getMentees(@Nullable @RequestParam("userId") Long userId) {
        if (userId != null) {
            var user = userService.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("No user with given id: " + userId);
            }
            return ResponseEntity.ok(mentorMenteeService.findByMentor(user.get()).stream().map(mapper::entityToDto));
        }
        return ResponseEntity.ok(StreamSupport.stream(mentorMenteeService.findAll().spliterator(), false).map(mapper::entityToDto));
    }

    @PostMapping("/mentorMentee")
    public ResponseEntity postMentorMentee(@Valid @RequestBody MentorMenteeDto mentorMentee, @RequestParam("userId") Long userId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user with given id: " + userId);
        }
        if (user.get().getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Permission denied!");
        }

        var mentor = userService.findById(mentorMentee.getMentor().getId());
        if (mentor.isEmpty()) {
            return ResponseEntity.status(404).body("No user with given id: " + mentorMentee.getMentor().getId());
        }

        var mentee = userService.findById(mentorMentee.getMentee().getId());
        if (mentee.isEmpty()) {
            return ResponseEntity.status(404).body("No user with given id: " + mentorMentee.getMentee().getId());
        }

        MentorMentee entity = new MentorMentee();
        entity.setMentee(mentee.get());
        entity.setMentor(mentor.get());

        return ResponseEntity.ok(mapper.entityToDto(mentorMenteeService.save(entity)));
    }

    @DeleteMapping("/mentorMentee")
    public ResponseEntity postMentorMentee(@RequestParam("id") Long id, @RequestParam("userId") Long userId) {
        var user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user with given id: " + userId);
        }
        if (user.get().getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Permission denied!");
        }

        if (!mentorMenteeService.existsById(id)) {
            return ResponseEntity.status(404).body("No Mentor Mentee with given id: " + id);
        }

        mentorMenteeService.deleteById(id);

        return ResponseEntity.ok("Successfully deleted");
    }

}
