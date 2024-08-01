package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService extends BaseService<User, Long>{

    private UserRepository userRepository;
    private TransactionItemService transactionItemService;

    public UserService(UserRepository userRepository, TransactionItemService transactionService) {
        this.userRepository = userRepository;
        this.transactionItemService = transactionService;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(this::calculateUserPoints);
        return user;
    }

    public Iterable<User> getAll() {
        calculateUserPointsForAllUsers();
        return findAll();
    }

    public Iterable<User> getAllYouth() {
        calculateUserPointsForAllUsers();
        return getYouth();
    }

    private Iterable<User> getYouth() {
        return userRepository.findAllBUKBySeason(LocalDate.now().getYear());
    }


    void calculateUserPointsForAllUsers() {
        getYouth().forEach(this::calculateUserPoints);
    }

    public Iterable<User> findAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public Iterable<User> findAllByTeam(Team t, Season s) {
        return userRepository.findAllByTeamAndSeasonAndGoal(t, s);
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

    public Optional<User> findByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    Long countAllByTeam(Team t, Integer season) {
        return userRepository.countAllByTeamAndSeasonAndGoal(t, season);
    }

    public void calculateUserPoints(User u) {
        u.setCurrentMyShareCredit(u.getBaseMyShareCredit());
        transactionItemService.findAllByUserAndSeasonYear(u, LocalDate.now().getYear()).forEach(t -> addTransaction(t, u));
        save(u);
    }

    private void addTransaction(TransactionItem t, User u) {
        if (t.getAccount().equals(Account.MYSHARE)) {
            u.setCurrentMyShareCredit(u.getCurrentMyShareCredit() + t.getCredit());
        }
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public User createEntity() {
        return new User();
    }
}
