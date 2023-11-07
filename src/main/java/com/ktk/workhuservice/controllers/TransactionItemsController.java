package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.TransactionItemDto;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.service.RoundService;
import com.ktk.workhuservice.service.TransactionItemService;
import com.ktk.workhuservice.service.TransactionService;
import com.ktk.workhuservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class TransactionItemsController {
    private TransactionItemService transactionItemService;
    private UserService userService;
    private RoundService roundService;
    private ModelMapper modelMapper;
    private TransactionService transactionService;

    public TransactionItemsController(TransactionItemService transactionItemService, UserService userService, RoundService roundService, ModelMapper modelMapper, TransactionService transactionService) {
        this.transactionItemService = transactionItemService;
        this.userService = userService;
        this.roundService = roundService;
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
    }

    @PostMapping("/transactionItem")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody TransactionItemDto transactionItem) {
        Optional<Transaction> transaction = transactionService.findById(transactionItem.getTransactionId());
        if (transaction.isEmpty()) {
            return ResponseEntity.status(400).body("No transaction found with id: " + transactionItem.getTransactionId());
        }

        Optional<User> user = userService.findById(transactionItem.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user found with id: " + transactionItem.getUser().getId());
        }

        Optional<User> createUser = userService.findById(transactionItem.getCreateUserId());
        if (createUser.isEmpty()) {
            return ResponseEntity.status(400).body("CreateUser not found by id: " + transactionItem.getCreateUserId());
        } else if (createUser.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("User is not allowed to create transactions");
        }

        transactionItemService.save(convertToEntity(transactionItem, user.get(), createUser.get()));
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/transactionItems")
    public ResponseEntity<?> addTransactions(@Valid @RequestBody List<TransactionItemDto> transactionItems) {
        transactionItems.forEach(this::addTransaction);
        return ResponseEntity.ok().body("Successfully added");

    }

    @DeleteMapping("/transactionItem")
    public ResponseEntity<?> deleteTransaction(@RequestParam("transactionItemId") Long transactionItemId, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied!");
        }
        if (transactionItemService.existsById(transactionItemId)) {
            transactionItemService.deleteById(transactionItemId);
            return ResponseEntity.status(200).body("Delete successful");
        }
        return ResponseEntity.status(403).body("No transaction item found with id:" + transactionItemId);

    }

    @GetMapping("/transactionItems")
    public ResponseEntity<?> getTransactionItems(@Nullable @RequestParam("userId") Long userId, @Nullable @RequestParam("transactionId") Long transactionId, @Nullable @RequestParam("roundId") Long roundId) {
        if (userId != null) {
            if (roundId != null) {
                Optional<Round> round = roundService.findById(roundId);
                if (round.isEmpty()) {
                    return ResponseEntity.status(404).body("No round with id:" + roundId);
                }
                return ResponseEntity.ok(StreamSupport.stream(transactionItemService.findAllByUserIdAndRound(userId, round.get()).spliterator(), false).map(this::convertToDto));
            }
            return ResponseEntity.ok(StreamSupport.stream(transactionItemService.findAllByUser(userId).spliterator(), false).map(this::convertToDto));
        }
        if (transactionId != null) {
            return ResponseEntity.ok(StreamSupport.stream(transactionItemService.findAllByTransactionId(transactionId).spliterator(), false).map(this::convertToDto));
        }

        return ResponseEntity.status(400).body("Empty parameters");

    }

    private TransactionItem convertToEntity(TransactionItemDto dto, User user, User createUser) {
        TransactionItem transaction = modelMapper.map(dto, TransactionItem.class);
        transaction.setUser(user);
        transaction.setCreateUser(createUser);
        return transaction;
    }

    private TransactionItemDto convertToDto(TransactionItem transaction) {
        TransactionItemDto dto = modelMapper.map(transaction, TransactionItemDto.class);
        dto.setUser(modelMapper.map(transaction.getUser(), UserDto.class));
        dto.setCreateUserId(transaction.getCreateUser().getId());
        return dto;
    }
}
