package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Activity;
import com.ktk.workhuservice.data.ActivityItem;
import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.ActivityItemDto;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.service.ActivityItemService;
import com.ktk.workhuservice.service.ActivityService;
import com.ktk.workhuservice.service.RoundService;
import com.ktk.workhuservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller()
@RequestMapping("/api")
public class ActivityItemController {
    private ActivityService activityService;
    private ActivityItemService activityItemService;
    private UserService userService;
    private RoundService roundService;
    private ModelMapper modelMapper;

    public ActivityItemController(ActivityService activityService, ActivityItemService activityItemService, UserService userService, RoundService roundService, ModelMapper modelMapper) {
        this.activityService = activityService;
        this.activityItemService = activityItemService;
        this.userService = userService;
        this.roundService = roundService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/activityItem")
    public ResponseEntity<?> addActivityItem(@Valid @RequestBody ActivityItemDto activityItem) {
        Optional<Activity> activity = activityService.findById(activityItem.getActivity().getId());
        if (activity.isEmpty()) {
            return ResponseEntity.status(400).body("No activity found with id: " + activityItem.getActivity().getId());
        }

        Optional<User> user = userService.findById(activityItem.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user found with id: " + activityItem.getUser().getId());
        }

        Optional<Round> round = roundService.findById(activityItem.getRound().getId());
        if (round.isEmpty()) {
            return ResponseEntity.status(400).body("No round found with id: " + activityItem.getRound().getId());
        }

        Optional<User> createUser = userService.findById(activityItem.getCreateUser().getId());
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("CreateUser not found by id: " + activityItem.getCreateUser());
        } else if (createUser.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("User is not allowed to create activitys");
        }

        activityItemService.save(convertToEntity(activityItem, user.get(), createUser.get()));
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/activityItems")
    public ResponseEntity<?> addActivityItems(@Valid @RequestBody List<ActivityItemDto> activityItems) {
        activityItems.forEach(this::addActivityItem);
        return ResponseEntity.ok().body("Successfully added");

    }

    @DeleteMapping("/activityItem")
    public ResponseEntity<?> deleteActivityItem(@RequestParam("activityItemId") Long activityItemId, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied!");
        }
        Optional<ActivityItem> item = activityItemService.findById(activityItemId);
        if (item.isPresent()) {
            if (item.get().getActivity().isRegisteredInApp()) {
                return ResponseEntity.status(400).body("Activity already registered. Can't modify.");
            }
            activityItemService.deleteById(activityItemId);
            return ResponseEntity.status(200).body("Delete successful");
        }

        return ResponseEntity.status(403).body("No activity item found with id:" + activityItemId);

    }

    @GetMapping("/activityItems")
    public ResponseEntity<?> getActivityItems(@Nullable @RequestParam("activityId") Long activityId) {
        if (activityId != null) {
            return ResponseEntity.ok(activityItemService.findByActivity(activityId).stream().map(this::convertToDto));
        }

        return ResponseEntity.status(400).body("Empty parameters");

    }

    private ActivityItem convertToEntity(ActivityItemDto dto, User user, User createUser) {
        ActivityItem activity = modelMapper.map(dto, ActivityItem.class);
        activity.setUser(user);
        activity.setCreateUser(createUser);
        activity.setCreateDateTime(LocalDateTime.now());
        return activity;
    }

    private ActivityItemDto convertToDto(ActivityItem activity) {
        ActivityItemDto dto = modelMapper.map(activity, ActivityItemDto.class);
        dto.setUser(modelMapper.map(activity.getUser(), UserDto.class));
        dto.setCreateUser(modelMapper.map(activity.getCreateUser(), UserDto.class));
        return dto;
    }
}
