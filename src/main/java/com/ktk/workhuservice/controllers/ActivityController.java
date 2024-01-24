package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Activity;
import com.ktk.workhuservice.service.ActivityService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private ActivityService activityService;
    private UserService userService;

    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    @GetMapping("/activities")
    public ResponseEntity getActivities(@Nullable @RequestParam("responsibleUserId") Long responsibleId,
                                        @Nullable @RequestParam("responsibleUserId") Long employerId,
                                        boolean paid, boolean registeredInApp,
                                        boolean registeredInMyShare,
                                        @Nullable @RequestParam("responsibleUserId") Long createUserId) {
        return ResponseEntity.status(200).body(activityService.fetchByQuery(responsibleId, employerId, paid, registeredInApp, registeredInMyShare, createUserId));
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
    public ResponseEntity postActivity(@Valid @RequestBody Activity activity) {
        return ResponseEntity.status(200).body(activityService.save(activity));
    }

    @PutMapping("/activity")
    public ResponseEntity putActivity(@Valid @RequestBody Activity activity, @RequestParam("activityId") Long activityId) {
        if (activityService.findById(activityId).isEmpty() || !activity.getId().equals(activityId)) {
            return ResponseEntity.status(400).body("Invalid activityId");
        }
        return ResponseEntity.status(200).body(activityService.save(activity));
    }

}
