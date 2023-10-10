package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.UserDto;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private TransactionItemService transactionItemService;
    private ModelMapper modelMapper;

    public UserService(UserRepository userRepository, TransactionItemService transactionService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.transactionItemService = transactionService;
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
        calculateCurrentMySharePoints(u);
        save(u);
        return u.getPoints();
    }

    private void calculateCurrentMySharePoints(User user) {
        transactionItemService.findAllByUser(user.getId()).forEach(t -> addTransaction(t, user));
    }


    private void addTransaction(TransactionItem t, User u) {
        if (t.getAccount().equals(Account.MYSHARE)) {
            u.setCurrentMyShareCredit(u.getCurrentMyShareCredit() + t.getCredit());
        }
    }

    public UserDto entityToDto(User u) {
        return modelMapper.map(u, UserDto.class);
    }
}
