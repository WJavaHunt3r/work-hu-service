package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Activity;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.ActivityDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.mapper.ActivityMapper;
import com.ktk.workhuservice.service.ActivityItemService;
import com.ktk.workhuservice.service.ActivityService;
import com.ktk.workhuservice.service.UserRoundService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private ActivityService activityService;
    private UserService userService;
    private ActivityMapper activityMapper;
    private ActivityItemService activityItemService;
    private UserRoundService userRoundService;

    public ActivityController(ActivityService activityService, UserService userService, ActivityMapper activityMapper, ActivityItemService activityItemService, UserRoundService userRoundService) {
        this.activityService = activityService;
        this.userService = userService;
        this.activityMapper = activityMapper;
        this.activityItemService = activityItemService;
        this.userRoundService = userRoundService;
    }

    @GetMapping("/activities")
    public ResponseEntity getActivities(@Nullable @RequestParam("responsibleUserId") Long responsibleId,
                                        @Nullable @RequestParam("employerId") Long employerId,
                                        @Nullable @RequestParam("registeredInApp") Boolean registeredInApp,
                                        @Nullable @RequestParam("registeredInMyShare") Boolean registeredInMyShare,
                                        @Nullable @RequestParam("createUserId") Long createUserId) {
        return ResponseEntity.status(200).body(activityService.fetchByQuery(responsibleId, employerId, registeredInApp, registeredInMyShare, createUserId));
    }

    @GetMapping("/activity")
    public ResponseEntity getActivity(@RequestParam("activityId") Long activityId) {
        var activity = activityService.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.status(404).body("No activity with id: " + activityId);
        }
        return ResponseEntity.status(200).body(activity.get());
    }

    @PostMapping("/activity")
    public ResponseEntity postActivity(@Valid @RequestBody ActivityDto activity) {
        Optional<User> createUser = userService.findById(activity.getCreateUser().getId());
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + activity.getCreateUser().getId());
        }
//        if (createUser.get().getRole().equals(Role.USER)) {
//            return ResponseEntity.status(403).body("Permission denied:");
//        }
        Optional<User> employer = userService.findById(activity.getEmployer().getId());
        if (employer.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + activity.getEmployer().getId());
        }
        Optional<User> responsibleUser = userService.findById(activity.getResponsible().getId());
        if (responsibleUser.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + activity.getResponsible().getId());
        }
        Activity entity = new Activity();
        entity.setCreateUser(createUser.get());
        entity.setResponsible(responsibleUser.get());
        entity.setEmployer(employer.get());
        return ResponseEntity.status(200).body(activityMapper.entityToDto(activityService.save(activityMapper.dtoToEntity(activity, entity))));
    }

    @PutMapping("/activity")
    public ResponseEntity putActivity(@Valid @RequestBody ActivityDto activityDto, @RequestParam("activityId") Long activityId) {
        Optional<Activity> activity = activityService.findById(activityId);
        if (activity.isEmpty() || !activityDto.getId().equals(activityId)) {
            return ResponseEntity.status(400).body("Invalid activityId");
        }
        return ResponseEntity.status(200).body(activityMapper.entityToDto(activityService.save(activityMapper.dtoToEntity(activityDto, activity.get()))));
    }

    @DeleteMapping("/activity")
    public ResponseEntity<?> deleteActivity(@RequestParam("activityId") Long activityId, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        Optional<Activity> item = activityService.findById(activityId);
        if (item.isPresent()) {
            if (!item.get().getCreateUser().getId().equals(user.get().getId()) && !user.get().getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(403).body("Permission denied!");
            }
            if (item.get().isRegisteredInApp() || item.get().isRegisteredInMyShare()) {
                return ResponseEntity.status(400).body("Activity already registered. Can't modify.");
            }
            activityItemService.deleteByActivityId(item.get().getId());
            activityService.deleteById(activityId);
            return ResponseEntity.status(200).body("Delete successful");
        }

        return ResponseEntity.status(403).body("No activity item found with id:" + activityId);

    }

    @GetMapping("/register")
    public ResponseEntity registerActivity(@RequestParam Long activityId, @RequestParam Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied:");
        }
        Optional<Activity> activity = activityService.findById(activityId);
        if (activity.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + activityId);
        }
        if (activity.get().isRegisteredInApp()) {
            return ResponseEntity.status(400).body("Activity already registered!");
        }

        try {
            activityService.registerActivity(activity.get(), user.get());
            userRoundService.findAll().forEach(userRoundService::calculateCurrentRoundPoints);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.toString());
        }

        return ResponseEntity.status(200).body("Registration successful");
    }

}
