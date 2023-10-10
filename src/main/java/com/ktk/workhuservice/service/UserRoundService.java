package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.*;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.UserRoundRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserRoundService {
    private UserRoundRepository userRoundRepository;
    private UserService userService;
    private TransactionItemService transactionItemService;
    private TransactionService transactionService;

    public UserRoundService(UserRoundRepository userRoundRepository, UserService userService, TransactionItemService transactionItemService, TransactionService transactionService) {
        this.userRoundRepository = userRoundRepository;
        this.userService = userService;
        this.transactionItemService = transactionItemService;
        this.transactionService = transactionService;
    }

    public UserRound findByUserAndRound(User u, Round r) {
        UserRound userRound = userRoundRepository.findByUserAndRound(u, r).orElseGet(() -> createUserRound(u, r));
        calculateCurrentRoundPoints(userRound);
        return userRound;
    }

    public UserRound save(UserRound userRound) {
        return userRoundRepository.save(userRound);
    }

    private UserRound createUserRound(User user, Round round) {
        UserRound newUserRound = new UserRound();
        newUserRound.setRound(round);
        newUserRound.setUser(user);
        return save(newUserRound);
    }

    public Iterable<UserRound> findByUser(User user) {
        user.setCurrentMyShareCredit(user.getBaseMyShareCredit());
        for(var u : userRoundRepository.findByUser(user)){
            calculateCurrentRoundPoints(u);
        }
        return userRoundRepository.findByUser(user);
    }

    public Iterable<UserRound> findByRound(Round r) {
        for(var u : userRoundRepository.findByRound(r)){
            calculateCurrentRoundPoints(u);
        }
        return userRoundRepository.findByRound(r);
    }

    public void calculateCurrentRoundPoints(UserRound userRound) {
        List<TransactionItem> myShareOnTrackItems = new ArrayList<>();
        List<TransactionItem> samvirkOnTrackItems = new ArrayList<>();

        userRound.setRoundPoints(0);
        userRound.setSamvirkPayments(0);
        userRound.setSamvirkPoints(0);
        userRound.setForbildePoints(0);
        User user = userRound.getUser();
        userService.calculateUserPoints(user);
        Round round = userRound.getRound();

        double points = 0;
        Iterable<TransactionItem> transactions = transactionItemService.findAllByUserIdAndRound(user.getId(), round);
        StreamSupport.stream(transactions.spliterator(), false).forEach(t -> addTransaction(t, user, userRound));

        if (!userRound.isMyShareOnTrackPoints() && user.getGoal() > 0 && ((double) user.getCurrentMyShareCredit() / user.getGoal()) * 100 > round.getMyShareGoal()) {
            userRound.setMyShareOnTrackPoints(true);
            myShareOnTrackItems.add(createOnTrackTransactionItem(user, round, "MyShare On Track", 50));
            points += 50;
        }

        if (userRound.getSamvirkPayments() > round.getSamvirkGoal()) {
            userRound.addSamvirkPoints(5);
            if (userRound.getSamvirkPoints() > 50) userRound.setSamvirkPoints(50);
            if (!userRound.isSamvirkOnTrackPoints()) {
                userRound.setSamvirkOnTrackPoints(true);
                samvirkOnTrackItems.add(createOnTrackTransactionItem(user, round, "Samvirk On Track", 5));
                points += 5;
            }
        }

        points += StreamSupport.stream(transactions.spliterator(), false).mapToDouble(TransactionItem::getPoints).sum();
        userRound.setRoundPoints(points);
        save(userRound);
        if (!myShareOnTrackItems.isEmpty()) saveOnTrackItems(myShareOnTrackItems, "MyShare On Track");
        if (!samvirkOnTrackItems.isEmpty()) saveOnTrackItems(samvirkOnTrackItems, "Samvirk On Track");
    }

    private void saveOnTrackItems(List<TransactionItem> transactionItems, String description) {
        Transaction newTransaction = new Transaction();
        newTransaction.setName(description);
        newTransaction.setAccount(Account.OTHER);
        newTransaction.setCreateDateTime(LocalDateTime.now());
        newTransaction.setCreateUser(userService.findAllByRole(Role.ADMIN).iterator().next());
        Transaction savedTransaction = transactionService.save(newTransaction);

        transactionItems.forEach(t -> t.setTransactionId(savedTransaction.getId()));

        transactionItemService.saveAll(transactionItems);
    }

    private TransactionItem createOnTrackTransactionItem(User u, Round r, String description, double points) {
        TransactionItem item = new TransactionItem();
        item.setAccount(Account.OTHER);
        item.setCredit(0);
        item.setHours(0);
        item.setUser(u);
        item.setRound(r);
        item.setPoints(points);
        item.setTransactionDate(LocalDate.now());
        item.setCreateDateTime(LocalDateTime.now());
        item.setTransactionType(TransactionType.POINT);
        item.setDescription(description);
        item.setCreateUser(userService.findAllByRole(Role.ADMIN).iterator().next());
        return item;
    }

    private void addTransaction(TransactionItem t, User u, UserRound userRound) {
        if (t.getAccount().equals(Account.SAMVIRK)) {
            userRound.addSamvirkPayment(t.getCredit());
            userRound.addSamvirkPoints((double) t.getCredit() / 1000);
        } else if (t.getAccount().equals(Account.OTHER) && t.getTransactionType().equals(TransactionType.VAER_ET_FORBILDE)) {
            userRound.addForbildePoints(t.getPoints());
        }
    }
}
