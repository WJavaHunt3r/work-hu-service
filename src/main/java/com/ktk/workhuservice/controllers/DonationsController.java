package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.donation.Donation;
import com.ktk.workhuservice.data.donation.DonationService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/donations")
public class DonationsController {

    private final DonationService donationService;
    private final UserService userService;

    public DonationsController(DonationService donationService, UserService userService) {
        this.donationService = donationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getDonations(@RequestParam("dateTime") String dateTime) {
        return ResponseEntity.status(200).body(donationService.fetchByQuery(dateTime));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonation(@PathVariable Long id) {
        var donation = donationService.findById(id);
        if (donation.isEmpty()) {
            return ResponseEntity.status(404).body("No donation with id: " + id);
        }
        return ResponseEntity.status(200).body(donation.get());
    }

    @PostMapping()
    public ResponseEntity<?> postDonation(@Valid @RequestBody Donation donation, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("No user found with id: " + userId);
        }
        if (!user.get().isAdmin()) {
            return ResponseEntity.status(404).body("Unauthorized request");
        }
        return ResponseEntity.status(200).body(donationService.save(donation));
    }

    @PutMapping("/{donationId}")
    public ResponseEntity<?> putDonation(@Valid @RequestBody Donation donation, @PathVariable Long donationId) {
        if (donationService.findById(donationId).isEmpty() || !donation.getId().equals(donationId)) {
            return ResponseEntity.status(400).body("Invalid donationId");
        }
        return ResponseEntity.status(200).body(donationService.save(donation));
    }

    @DeleteMapping("/{donationId}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long donationId) {
        if (donationService.findById(donationId).isEmpty()) {
            return ResponseEntity.status(400).body("Invalid donationId");
        }
        donationService.deleteById(donationId);
        return ResponseEntity.status(200).body("Delete Successful");
    }
}
