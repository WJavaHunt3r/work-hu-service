package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.*;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoundService roundService;
    private UserRoundService userRoundService;
    private TransactionItemService transactionItemService;
    private TransactionService transactionService;
    private ModelMapper modelMapper;

    public UserService(UserRepository userRepository, RoundService roundService, UserRoundService userRoundService, TransactionItemService transactionItemService, TransactionService transactionService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roundService = roundService;
        this.userRoundService = userRoundService;
        this.transactionItemService = transactionItemService;
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    long count() {
        return userRepository.count();
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(this::calculateUserPoints);
        return user;
    }

    public User save(User u) {
        return userRepository.save(u);
    }

    public Iterable<User> getAll() {
        calculateUserPointsForAllUsers();
        return userRepository.findAll();
    }

    public Iterable<User> getAllYouth() {
        calculateUserPointsForAllUsers();
        return getYouth();
    }

    private Iterable<User> getYouth() {
        return userRepository.findAllByGoalGreaterThan(0);
    }

    void calculateUserPointsForAllUsers() {
        getYouth().forEach(this::calculateUserPoints);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public Iterable<User> findAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public Iterable<User> findAllByTeam(Team t) {
        return userRepository.findAllByTeam(t);
    }

    public Optional<User> findByMyShareId(Long id) {
        Optional<User> user = userRepository.findByMyShareID(id);
        user.ifPresent(this::calculateUserPoints);
        return user;
    }

    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(this::calculateUserPoints);
        return user;
    }

    int countAllByTeam(Team t) {
        return userRepository.countAllByTeamAndGoalGreaterThan(t, 0);
    }

    public double calculateUserPoints(User u) {
        u.setPoints(0);
        u.setCurrentMyShareCredit(u.getBaseMyShareCredit());
        for (Round round : roundService.getAll()) {
            u.addPoints(calculateCurrentRoundPoints(u, round));
        }
        save(u);
        return u.getPoints();
    }

    public double calculateCurrentRoundPoints(User user, Round round) {
        List<TransactionItem> myShareOnTrackItems = new ArrayList<>();
        List<TransactionItem> samvirkOnTrackItems = new ArrayList<>();

        var userRound = getUserRound(user, round);
        double points = 0;
        Iterable<TransactionItem> transactions = transactionItemService.findAllByUserIdAndRound(user.getId(), round);
        StreamSupport.stream(transactions.spliterator(), false).forEach(t -> addTransaction(t, user, userRound));

        if (!userRound.isMyShareOnTrackPoints() && user.getGoal() > 0 && ((double) user.getCurrentMyShareCredit() / user.getGoal()) * 100 > round.getMyShareGoal()) {
            userRound.setMyShareOnTrackPoints(true);
            myShareOnTrackItems.add(createOnTrackTransaction(user, round, "MyShare On Track", 50));
            points += 50;
        }

        if (!userRound.isSamvirkOnTrackPoints() && userRound.getSamvirkPayments() > round.getSamvirkGoal()) {
            userRound.addSamvirkPoints(12);
            userRound.setSamvirkOnTrackPoints(true);
            samvirkOnTrackItems.add(createOnTrackTransaction(user, round, "Samvirk On Track", 12));
            points += 12;
        }

        points += StreamSupport.stream(transactions.spliterator(), false).mapToDouble(TransactionItem::getPoints).sum();
        userRound.setRoundPoints(points);
        userRoundService.save(userRound);
        if (!myShareOnTrackItems.isEmpty()) saveOnTrackItems(myShareOnTrackItems, "MyShare On Track");
        if (!samvirkOnTrackItems.isEmpty()) saveOnTrackItems(samvirkOnTrackItems, "Samvirk On Track");
        return points;
    }

    private void saveOnTrackItems(List<TransactionItem> transactionItems, String description) {
        Transaction newTransaction = new Transaction();
        newTransaction.setName(description);
        newTransaction.setAccount(Account.OTHER);
        newTransaction.setCreateDateTime(LocalDateTime.now());
        newTransaction.setCreateUser(findAllByRole(Role.ADMIN).iterator().next());
        Transaction savedTransaction = transactionService.save(newTransaction);

        transactionItems.forEach(t -> t.setTransactionId(savedTransaction.getId()));

        transactionItemService.saveAll(transactionItems);
    }

    private TransactionItem createOnTrackTransaction(User u, Round r, String description, double points) {
        TransactionItem transaction = new TransactionItem();
        transaction.setAccount(Account.OTHER);
        transaction.setCredit(0);
        transaction.setHours(0);
        transaction.setUser(u);
        transaction.setRound(r);
        transaction.setPoints(points);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCreateDateTime(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.POINT);
        transaction.setDescription(description);
        transaction.setCreateUser(findAllByRole(Role.ADMIN).iterator().next());
        return transaction;
    }

    private UserRound getUserRound(User user, Round round) {
        Optional<UserRound> userRound = userRoundService.findByUserAndRound(user, round);
        if (userRound.isEmpty()) {
            return userRoundService.createUserRound(user, round);
        }
        return userRound.get();
    }

    private void addTransaction(TransactionItem t, User u, UserRound userRound) {
        if (t.getAccount().equals(Account.MYSHARE)) {
            u.setCurrentMyShareCredit(u.getCurrentMyShareCredit() + t.getCredit());
        } else if (t.getAccount().equals(Account.SAMVIRK)) {
            userRound.addSamvirkPayment(t.getCredit());
        } else if (t.getAccount().equals(Account.OTHER) && t.getTransactionType().equals(TransactionType.VAER_ET_FORBILDE)) {
            userRound.addForbildePoints(t.getPoints());
        }
    }

    public UserDto entityToDto(User u) {
        return modelMapper.map(u, UserDto.class);
    }
}
