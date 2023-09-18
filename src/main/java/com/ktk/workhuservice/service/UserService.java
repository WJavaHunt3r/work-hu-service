package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
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

    public void save(User u) {
        userRepository.save(u);
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

    public Optional<User> findByMyShareId(Long id){
        return userRepository.findByMyShareID(id);
    }

    public int countAllByTeam(Team t) {
        return userRepository.countAllByTeam(t);
    }

    public void calculateUserPoints(User u) {
        Optional<Season> currentSeason = seasonService.findCurrentSeason();
        if (currentSeason.isPresent()) {
            double points = 0;
            Iterable<Transaction> transactions = transactionService.getAllByUserAndSeason(u, currentSeason.get());
            StreamSupport.stream(transactions.spliterator(), false).forEach(this::addTransaction);

            if (((double) u.getCurrentMyShareCredit() / u.getGoal()) * 100 > currentSeason.get().getMyShareGoal()) {
                points += 50;
            }

            if (u.getBaseSamvirkCredit() != null && u.getBaseSamvirkCredit() > currentSeason.get().getSamvirkGoal()) {
                points += 12;
            }


            points += StreamSupport.stream(transactions.spliterator(), false).map(Transaction::getPoints).count();

            u.setPoints(points);
            save(u);
        }
    }

    public void addTransaction(Transaction t){
        User u = t.getUser();
        if (t.getAccount().equals(Account.MYSHARE)) {
            u.setCurrentMyShareCredit(u.getBaseMyShareCredit() + t.getValue());
        } else {
            u.setBaseSamvirkCredit(u.getBaseSamvirkCredit() + t.getValue());
        }
    }

    private void addExtraPoints(User u){

    }
}
