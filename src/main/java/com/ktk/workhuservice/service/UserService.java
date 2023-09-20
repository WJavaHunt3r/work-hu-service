package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private UserRepository userRepository;
    private SeasonService seasonService;
    private TransactionService transactionService;

    public UserService(UserRepository userRepository, SeasonService seasonService, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.seasonService = seasonService;
        this.transactionService = transactionService;
    }

    public long count() {
        return userRepository.count();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User u) {
        return userRepository.save(u);
    }

    public Iterable<User> getAll() {
        return userRepository.findAll();
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

    public int countAllByTeam(Team t) {
        return userRepository.countAllByTeamAndGoalGreaterThan(t, 0);
    }

    public void calculateUserPoints(User u) {
        Optional<Season> currentSeason = seasonService.findCurrentSeason();
        if (currentSeason.isPresent()) {
            double points = 0;
            Iterable<Transaction> transactions = transactionService.getAllByUserAndSeason(u, currentSeason.get());
            StreamSupport.stream(transactions.spliterator(), false).forEach(t -> addTransaction(t, u));

            if (u.getGoal() > 0 && ((double) u.getCurrentMyShareCredit() / u.getGoal()) * 100 > currentSeason.get().getMyShareGoal()) {
                points += 50;
            }

            if (u.getBaseSamvirkCredit() != null && u.getBaseSamvirkCredit() > currentSeason.get().getSamvirkGoal()) {
                points += 12;
            }

            points += StreamSupport.stream(transactions.spliterator(), false).mapToDouble(Transaction::getPoints).sum();

            u.setPoints(points);
        }
    }

    public void addTransaction(Transaction t, User u) {
            if (t.getAccount().equals(Account.MYSHARE)) {
                u.setCurrentMyShareCredit(u.getCurrentMyShareCredit() + (t.getTransactionType().equals(TransactionType.CASH) ? t.getValue() : t.getValue() * 2000));
            } else {
                u.setBaseSamvirkCredit(u.getBaseSamvirkCredit() + t.getValue());
            }
    }

    private void addExtraPoints(User u) {

    }
}
