package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.TransactionDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.service.TransactionItemService;
import com.ktk.workhuservice.service.TransactionService;
import com.ktk.workhuservice.service.UserRoundService;
import com.ktk.workhuservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class TransactionsController {

    private TransactionService transactionService;
    private ModelMapper modelMapper;
    private TransactionItemService transactionItemService;
    private UserService userService;
    private UserRoundService userRoundService;

    public TransactionsController(TransactionService transactionService, ModelMapper modelMapper, TransactionItemService transactionItemService, UserService userService, UserRoundService userRoundService) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
        this.transactionItemService = transactionItemService;
        this.userService = userService;
        this.userRoundService = userRoundService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody TransactionDto transaction, @RequestParam("userId") Long userId) {
        Optional<User> createUser = findById(userId);
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (createUser.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied:");
        }
        return ResponseEntity.status(200).body(convertToDto(transactionService.save(convertToEntity(transaction, createUser.get()))));
    }

    @DeleteMapping("/transaction")
    public ResponseEntity<?> deleteTransaction(@RequestParam("transactionId") Long transactionId, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied!");
        }
        if (transactionService.existsById(transactionId)) {
            Iterable<TransactionItem> items = transactionItemService.findAllByTransactionId(transactionId);
            transactionService.deleteById(transactionId);
            transactionItemService.deleteByTransactionId(transactionId);
            for (var item : items) {
                userRoundService.calculateCurrentRoundPoints(userRoundService.findByUserAndRound(item.getUser(), item.getRound()));
            }

            return ResponseEntity.status(200).body("Delete successful");
        }
        return ResponseEntity.status(403).body("No transaction found with id:" + transactionId);

    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@Nullable @RequestParam("createUserId") Long createUserId) {
        if (createUserId != null) {
            Optional<User> createUser = userService.findById(createUserId);
            if (createUser.isPresent()) {
                return ResponseEntity.status(200).body(StreamSupport.stream(transactionService.findAllByCreateUser(createUser.get()).spliterator(), false).map(this::convertToDto));
            }

        }
        return ResponseEntity.status(200).body(StreamSupport.stream(transactionService.findAll().spliterator(), false).map(this::convertToDto));
    }

    @GetMapping("/transaction/:id")
    public ResponseEntity<?> getTransaction(@RequestParam Long transactionId) {
        Optional<Transaction> transaction = transactionService.findById(transactionId);
        if (transaction.isEmpty()) {
            return ResponseEntity.status(400).body("No transaction found with Id: " + transactionId);
        }
        return ResponseEntity.status(200).body(convertToDto(transaction.get()));
    }

    private Transaction convertToEntity(TransactionDto dto, User user) {
        Transaction transaction = modelMapper.map(dto, Transaction.class);
        transaction.setCreateUser(user);
        return transaction;
    }

    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = modelMapper.map(transaction, TransactionDto.class);
        dto.setTransactionCount(transactionItemService.countByTransactionId(transaction.getId()));
        return dto;
    }

    private Optional<User> findById(Long id) {
        return userService.findById(id);
    }
}
