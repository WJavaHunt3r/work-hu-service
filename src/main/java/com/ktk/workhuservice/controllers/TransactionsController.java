package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.service.SeasonService;
import com.ktk.workhuservice.service.TransactionService;
import com.ktk.workhuservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransactionsController {
    private TransactionService transactionService;
    private UserService userService;
    private SeasonService seasonService;

    public TransactionsController(TransactionService transactionService, UserService userService, SeasonService seasonService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.seasonService = seasonService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction transaction) {
//        Optional<User> user = userService.findByMyShareId(transaction.getUser().getMyShareID());
//        Optional<User> createUser = userService.findByMyShareId(transaction.getCreateUser().getMyShareID());
//        Optional<Season> season = seasonService.findBySeasonNumber(transaction.getSeason().getSeasonNumber());
//        if (user.isPresent()) {
//            transaction.setUser(user.get());
//        } else {
//            return ResponseEntity.status(400).body("User not found");
//        }
//        if (createUser.isPresent()) {
//            transaction.setCreateUser(createUser.get());
//        } else {
//            return ResponseEntity.status(400).body("CreateUser not found");
//        }
//        if (season.isPresent()) {
//            transaction.setSeason(season.get());
//        } else {
//            return ResponseEntity.status(400).body("Season not found");
//        }
        transactionService.save(transaction);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionsByUserId(@RequestParam Long userId){
        return ResponseEntity.ok(transactionService.getAllByUser(userId));
    }
}
