package com.ktk.workhuservice.controllers;

import com.ktk.workhuservice.data.paceteamround.PaceTeamRoundService;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.transactionitems.TransactionItem;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.transactions.Transaction;
import com.ktk.workhuservice.data.transactions.TransactionService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.dto.TransactionItemDto;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Role;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/transactionItem")
public class TransactionItemsController {
    private TransactionItemService transactionItemService;
    private UserService userService;
    private RoundService roundService;
    private ModelMapper modelMapper;
    private TransactionService transactionService;
    private PaceTeamRoundService paceTeamRoundService;

    public TransactionItemsController(TransactionItemService transactionItemService, UserService userService, RoundService roundService, ModelMapper modelMapper, TransactionService transactionService, PaceTeamRoundService paceTeamRoundService) {
        this.transactionItemService = transactionItemService;
        this.userService = userService;
        this.roundService = roundService;
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
        this.paceTeamRoundService = paceTeamRoundService;
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@Valid @RequestBody TransactionItemDto transactionItem) {
        Optional<Transaction> transaction = transactionService.findById(transactionItem.getTransactionId());
        if (transaction.isEmpty()) {
            return ResponseEntity.status(400).body("No transaction found with id: " + transactionItem.getTransactionId());
        }

        Optional<User> user = userService.findById(transactionItem.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user found with id: " + transactionItem.getUser().getId());
        }

        Optional<Round> round = roundService.findById(transactionItem.getRound().getId());
        if (round.isEmpty()) {
            return ResponseEntity.status(400).body("No round found with id: " + transactionItem.getRound().getId());
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

    @PostMapping("/items")
    public ResponseEntity<?> addTransactions(@Valid @RequestBody List<TransactionItemDto> transactionItems) {
        transactionItems.forEach(this::addTransaction);
        paceTeamRoundService.calculateAllTeamRoundPoints();
        return ResponseEntity.ok().body("Successfully added");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, @RequestParam("userId") Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("No user with id:" + userId);
        }
        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.status(403).body("Permission denied!");
        }
        Optional<TransactionItem> item = transactionItemService.findById(id);
        if (item.isPresent()) {
            transactionItemService.deleteById(id);
            paceTeamRoundService.calculateAllTeamRoundPoints(item.get().getRound());
            return ResponseEntity.status(200).body("Delete successful");
        }

        return ResponseEntity.status(403).body("No transaction item found with id:" + id);

    }

    @GetMapping
    public ResponseEntity<?> getTransactionItems(@Nullable @RequestParam("userId") Long userId, @Nullable @RequestParam("transactionId") Long transactionId, @Nullable @RequestParam("roundId") Long roundId) {
        if (userId != null) {
            Optional<User> user = userService.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(400).body("No user found with id: " + userId);
            }
            if (roundId != null) {
                Optional<Round> round = roundService.findById(roundId);
                if (round.isEmpty()) {
                    return ResponseEntity.status(404).body("No round with id:" + roundId);
                }
                return ResponseEntity.ok(StreamSupport.stream(transactionItemService.findAllByUserAndRound(user.get(), round.get()).spliterator(), false).map(this::convertToDto));
            }
            return ResponseEntity.ok(StreamSupport.stream(transactionItemService.findAllByUser(user.get()).spliterator(), false).map(this::convertToDto));
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
