package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.*;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.UserRoundRepository;
import com.ktk.workhuservice.service.microsoft.MicrosoftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserRoundService extends BaseService<UserRound, Long> {
    private UserRoundRepository userRoundRepository;
    private UserService userService;
    private TransactionItemService transactionItemService;
    private TransactionService transactionService;
    private RoundService roundService;
    private MicrosoftService microsoftService;

    @Autowired
    private GoalService goalService;

    public UserRoundService(UserRoundRepository userRoundRepository, UserService userService, TransactionItemService transactionItemService, TransactionService transactionService, RoundService roundService, MicrosoftService microsoftService) {
        this.userRoundRepository = userRoundRepository;
        this.userService = userService;
        this.transactionItemService = transactionItemService;
        this.transactionService = transactionService;
        this.roundService = roundService;
        this.microsoftService = microsoftService;
    }

    public UserRound findByUserAndRound(User u, Round r) {
        UserRound userRound = userRoundRepository.findByUserAndRound(u, r).orElseGet(() -> createUserRound(u, r));
        calculateCurrentRoundPoints(userRound);
        return userRound;
    }

    private UserRound createUserRound(User user, Round round) {
        UserRound newUserRound = new UserRound();
        newUserRound.setRound(round);
        newUserRound.setUser(user);
        return save(newUserRound);
    }

    public List<UserRound> findByUserAndSeasonYear(User user, Integer season) {
        for (var u : userRoundRepository.findByUserAndSeason(user, season)) {
            calculateCurrentRoundPoints(u);
        }
        return userRoundRepository.findByUserAndSeason(user, season);
    }

    public List<UserRound> findByUser(User user) {
        for (var u : userRoundRepository.findByUser(user)) {
            calculateCurrentRoundPoints(u);
        }
        return userRoundRepository.findByUser(user);
    }

    void getUserRoundsOrCreate(Round round) {
        userService.getAllYouth().forEach(u -> findByUserAndRound(u, round));
    }

    public Iterable<UserRound> findByRound(Round r) {
        return userRoundRepository.findByRound(r);
    }

    Iterable<UserRound> findByRoundAndTeam(Round r, Team t) {
        return userRoundRepository.findByRoundAndTeam(r, t);
    }

    public void calculateCurrentRoundPoints(UserRound userRound) {
        Optional<Round> currentRound = roundService.findRoundByDate(LocalDateTime.now());
        if (currentRound.isPresent() && currentRound.get() == userRound.getRound()) {
            List<TransactionItem> myShareOnTrackItems = new ArrayList<>();
            List<TransactionItem> samvirkOnTrackItems = new ArrayList<>();

            userRound.setRoundPoints(0);
            userRound.setSamvirkPayments(0);
            userRound.setSamvirkPoints(0);
            userRound.setBMMPerfectWeekPoints(0);
            User user = userRound.getUser();
            Round round = userRound.getRound();
            Optional<Goal> userGoal = goalService.findByUserAndSeason(user, round.getSeason().getSeasonYear());
            if (userGoal.isEmpty()) {
                return;
            }

            Integer goal = userGoal.get().getGoal();

            double points = 0;

            String myShareOnTrackName = "MyShare On Track Round " + round.getRoundNumber() + "(" + round.getSeason().getSeasonYear() + ")";
            String samvirkOnTrackName = "Samvirk On Track Round " + round.getRoundNumber() + "(" + round.getSeason().getSeasonYear() + ")";

            if (!userRound.isMyShareOnTrackPoints() && goal > 0 && ((double) user.getCurrentMyShareCredit() / goal) * 100 >= round.getMyShareGoal()) {
                userRound.setMyShareOnTrackPoints(true);
                myShareOnTrackItems.add(createOnTrackTransactionItem(user, round, myShareOnTrackName, 50));
                points += 50;
            } else if (userRound.isMyShareOnTrackPoints() && goal > 0 && ((double) user.getCurrentMyShareCredit() / goal) * 100 < round.getMyShareGoal()) {
                userRound.setMyShareOnTrackPoints(false);
                myShareOnTrackItems.add(createOnTrackTransactionItem(user, round, myShareOnTrackName + " revert", -50));
                points -= 50;
            }

            double maxPoints = round.getSamvirkMaxPoints();
            double onTrackPoints = round.getSamvirkOnTrackPoints();
            if (userRound.getSamvirkPayments() >= round.getSamvirkGoal()) {

                if (!userRound.isSamvirkOnTrackPoints()) {
                    userRound.addSamvirkPoints(onTrackPoints);
                    if (maxPoints != 0 && userRound.getSamvirkPoints() > maxPoints) userRound.setSamvirkPoints(maxPoints);
                    userRound.setSamvirkOnTrackPoints(true);
                    samvirkOnTrackItems.add(createOnTrackTransactionItem(user, round, samvirkOnTrackName, onTrackPoints));
                    points += onTrackPoints;
                }
            }
            if (userRound.getSamvirkPayments() < round.getSamvirkGoal() && userRound.isSamvirkOnTrackPoints()) {
                userRound.setSamvirkOnTrackPoints(false);
                samvirkOnTrackItems.add(createOnTrackTransactionItem(user, round, samvirkOnTrackName + " revert", onTrackPoints * -1));
                points -= onTrackPoints;
            }

            if (!myShareOnTrackItems.isEmpty()) saveOnTrackItems(myShareOnTrackItems, myShareOnTrackName);
            if (!samvirkOnTrackItems.isEmpty()) saveOnTrackItems(samvirkOnTrackItems, samvirkOnTrackName);

            Iterable<TransactionItem> transactions = transactionItemService.findAllByUserAndRound(user, round);
            transactions.forEach(t -> addTransaction(t, userRound));

            points += StreamSupport.stream(transactions.spliterator(), false).mapToDouble(TransactionItem::getPoints).sum();
            userRound.setRoundPoints(points);
            save(userRound);

            userService.calculateUserPoints(user);
        }
    }

    private void saveOnTrackItems(List<TransactionItem> transactionItems, String description) {
        Optional<Transaction> onTrackTransaction = transactionService.findByName(description);
        if (onTrackTransaction.isEmpty()) {
            Transaction newTransaction = new Transaction();
            newTransaction.setName(description);
            newTransaction.setAccount(Account.OTHER);
            newTransaction.setCreateDateTime(LocalDateTime.now());
            newTransaction.setCreateUser(userService.findAllByRole(Role.ADMIN).iterator().next());
            Transaction savedTransaction = transactionService.save(newTransaction);

            transactionItems.forEach(t -> t.setTransactionId(savedTransaction.getId()));
        } else {
            transactionItems.forEach(t -> t.setTransactionId(onTrackTransaction.get().getId()));
        }

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

    private void addTransaction(TransactionItem t, UserRound userRound) {
        if (t.getAccount().equals(Account.SAMVIRK)) {
            userRound.addSamvirkPayment(t.getCredit());
            userRound.addSamvirkPoints((double) t.getCredit() / 1000);
        } else if (t.getAccount().equals(Account.OTHER) && t.getTransactionType().equals(TransactionType.BMM_PERFECT_WEEK)) {
            userRound.addBMMPerfectWeekPoints(t.getPoints());
        }
    }

    double calculateTeamRoundSamvirkPayments(Team team, Round round) {
        return userRoundRepository.calculateTeamRoundSamvirkPayments(team, round);
    }

    public double calculateTeamRoundPoints(Team team, Round round) {
        return userRoundRepository.calculateTeamRoundPoints(team, round);
    }

    @Override
    protected JpaRepository<UserRound, Long> getRepository() {
        return userRoundRepository;
    }

    @Override
    public Class<UserRound> getEntityClass() {
        return UserRound.class;
    }

    @Override
    public UserRound createEntity() {
        return new UserRound();
    }

    @Scheduled(cron = "0 0 17 ? * TUE")
    private void sendOnTrackEmails() {
        Round currentRound = roundService.getLastRound();
        for (User u : userService.getAllYouth()) {
            Optional<UserRound> ur = userRoundRepository.findByUserAndRound(u, currentRound);
            Optional<Goal> goal = goalService.findByUserAndSeason(u, LocalDate.now().getYear());
            if (goal.isPresent() && ur.isPresent()) {
                double currentUserGoal = goal.get().getGoal() * (currentRound.getMyShareGoal() / 100.0);
                if (u.getCurrentMyShareCredit() < currentUserGoal) {
                    try {
//                        if (u.getId() == 255) {
                        microsoftService.sendStatusUpdate(u.getCurrentMyShareCredit(), (double) u.getCurrentMyShareCredit() / goal.get().getGoal() * 100, (int) currentUserGoal - u.getCurrentMyShareCredit(), u, currentRound);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
