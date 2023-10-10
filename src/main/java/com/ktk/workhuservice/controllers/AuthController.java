package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.ChangePasswordDto;
import com.ktk.workhuservice.dto.LoginDto;
import com.ktk.workhuservice.dto.ResetPasswordDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.security.SecurityUtils;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));

        return ResponseEntity.status(200).body(authentication.getPrincipal());
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {

        Optional<User> user = userService.findByUsername(dto.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("Username not found");
        }

        if (!user.get().getPassword().equals(dto.getOldPassword())) {
            return ResponseEntity.status(400).body("Wrong password");
        }

        if (user.get().getPassword().equals(dto.getNewPassword())) {
            return ResponseEntity.status(400).body("New password cannot be same as old password");
        }

        user.get().setPassword(dto.getNewPassword());
        user.get().setChangedPassword(true);

        userService.save(user.get());

        return ResponseEntity.status(200).body("Password change successful");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {

        Optional<User> user = userService.findById(dto.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id: " + dto.getUserId());
        }

        Optional<User> changer = userService.findById(dto.getChangerId());
        if (changer.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id: " + dto.getChangerId());
        }
        if( changer.get().getRole() != Role.ADMIN){
            return ResponseEntity.status(400).body("User is not admin");
        }

        user.get().setPassword(SecurityUtils.encryptSecret(user.get().getUsername()));
        user.get().setChangedPassword(false);

        userService.save(user.get());

        return ResponseEntity.status(200).body("Password reset successful");
    }

}
