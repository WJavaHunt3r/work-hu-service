package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.payments.Payment;
import com.ktk.workhuservice.data.payments.PaymentsService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.dto.PaymentDto;
import com.ktk.workhuservice.enums.PaymentStatus;
import com.ktk.workhuservice.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsService paymentService;
    private final PaymentMapper paymentMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getPayments(@Nullable @RequestParam("userId") Long userId,
                                         @Nullable @RequestParam("status") PaymentStatus status,
                                         @Nullable @RequestParam("donationId") Long donationId) {
        return ResponseEntity.status(200).body(paymentService.fetchByQuery(status, userId, donationId).stream().map(paymentMapper::entityToDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        var payment = paymentService.findById(id);
        if (payment.isEmpty()) {
            return ResponseEntity.status(404).body("No payment with id: " + id);
        }
        return ResponseEntity.status(200).body(paymentMapper.entityToDto(payment.get()));
    }

    @PostMapping
    public ResponseEntity<?> postPayment(@Valid @RequestBody PaymentDto payment) {
        Payment entity = new Payment();
        if (payment.getUser() != null) {
            Optional<User> createUser = userService.findById(payment.getUser().getId());
            if (createUser.isEmpty()) {
                return ResponseEntity.status(400).body("No user with id:" + payment.getUser().getId());
            }
            entity.setUser(createUser.get());
        }

        return ResponseEntity.status(200).body(paymentService.save(paymentMapper.dtoToEntity(payment, entity)));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<?> putPayment(@Valid @RequestBody Payment payment, @PathVariable Long paymentId) {
        if (paymentService.findById(paymentId).isEmpty() || !payment.getId().equals(paymentId)) {
            return ResponseEntity.status(400).body("Invalid paymentId");
        }
        return ResponseEntity.status(200).body(paymentService.save(payment));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable Long paymentId) {
        if (paymentService.findById(paymentId).isPresent()) {
            return ResponseEntity.status(400).body("Invalid paymentId");
        }
        paymentService.deleteById(paymentId);
        return ResponseEntity.status(200).body("Delete Successful");
    }
}
