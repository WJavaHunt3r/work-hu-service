package com.ktk.workhuservice.data.paceuserround;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.transactionitems.TransactionItem;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.transactions.Transaction;
import com.ktk.workhuservice.data.transactions.TransactionService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.data.userstatus.UserStatus;
import com.ktk.workhuservice.data.userstatus.UserStatusService;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.service.BaseService;
import com.ktk.workhuservice.service.microsoft.MicrosoftService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaceUserRoundService extends BaseService<PaceUserRound, Long> {
    private final PaceUserRoundRepository repository;
    private final TransactionItemService transactionItemService;
    private final RoundService roundService;
    private final MicrosoftService microsoftService;
    private final UserStatusService userStatusService;

    private final UserService userService;
    private final TransactionService transactionService;

    public PaceUserRoundService(PaceUserRoundRepository repository, TransactionItemService transactionItemService, RoundService roundService, MicrosoftService microsoftService, UserStatusService userStatusService, UserService userService, TransactionService transactionService) {
        this.repository = repository;
        this.transactionItemService = transactionItemService;
        this.roundService = roundService;
        this.microsoftService = microsoftService;
        this.userStatusService = userStatusService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    public List<PaceUserRound> findByQuery(Long userId, Long roundId, Integer seasonYear, Long paceTeamId) {
        return repository.findByQuery(userId, roundId, seasonYear, paceTeamId);
    }

    public Optional<PaceUserRound> findByUserAndRound(User u, Round r) {
        return repository.findByUserAndRound(u, r);
    }

    public int countByRoundAndTeam(Round r, PaceTeam t) {
        return repository.countByRoundAndTeam(r, t);
    }

    public Integer calculatePaceTeamRoundCoins(PaceTeam t, Round round) {
        return repository.calculatePaceTeamRoundCoins(t, round);
    }

    public Double sumByUserAndSeason(User u, Integer seasonYear) {
        return repository.sumByUserAndSeason(u, seasonYear);
    }

    public void createAllPaceUserRounds(Round round) {
        for (UserStatus us : userStatusService.fetchByQuery(round.getSeason().getSeasonYear(), null)) {
            userStatusService.calculateUserStatus(us);
            Optional<PaceUserRound> pur = findByUserAndRound(us.getUser(), round);
            if (pur.isPresent()) {
                calculateUserRoundStatus(round, us.getUser());
            } else {
                save(createPaceUserRound(us.getUser(), round));
            }
        }
        round.setUserRoundsCreated(true);
    }

    public void calculateUserRoundStatus(User u) {
        calculateUserRoundStatus(roundService.getLastRound(), u);
    }

    public void calculateUserRoundStatus(Round r, User u) {
        findByUserAndRound(u, r).ifPresent(pur -> {
            calculateUserRoundStatus(pur);
            save(pur);
        });
    }

    public void createPaceUserRound(User u) {
        save(createPaceUserRound(u, roundService.getLastRound()));
    }

    private PaceUserRound createPaceUserRound(User u, Round round) {
        PaceUserRound pur = new PaceUserRound();
        pur.setRound(round);
        pur.setUser(u);
        pur.setRoundCredits(0);
        pur.setRoundCoins(0.0);
        pur.setOnTrack(false);
        calculateUserRoundStatus(pur);

        return pur;
    }

    private int calculateCurrRoundMyShareGoal(Round round, User u) {
        Optional<UserStatus> us = userStatusService.findByUserId(u.getId(), round.getSeason().getSeasonYear());
        return us.map(userStatus -> Math.max(0, (int) Math.round(userStatus.getGoal() * (round.getMyShareGoal() / 100)) - userStatus.getTransactions())).orElse(0);
    }

    private void calculateUserRoundStatus(PaceUserRound pur) {
        pur.setRoundMyShareGoal(calculateCurrRoundMyShareGoal(pur.getRound(), pur.getUser()));
        pur.setRoundCoins(0.0);
        Optional<UserStatus> us = userStatusService.findByUserId(pur.getUser().getId(), pur.getRound().getSeason().getSeasonYear());
        String myShareOnTrackName = "MyShare On Track " + pur.getRound().getRoundNumber() + ". hét (" + pur.getRound().getSeason().getSeasonYear() + ")";
        if(pur.getRound() == roundService.getLastRound() && us.isPresent()){
            if (us.get().getStatus() * 100 >= pur.getRound().getMyShareGoal() && !pur.isMyShareOnTrackPoints()) {
                pur.setOnTrack(true);
                if (!pur.isMyShareOnTrackPoints()) {
                    saveOnTrackItems(createOnTrackTransactionItem(pur.getUser(), pur.getRound(), myShareOnTrackName, 10), myShareOnTrackName);
                    pur.setMyShareOnTrackPoints(true);
                }
            } else if (us.get().getStatus() * 100 < pur.getRound().getMyShareGoal() && pur.isMyShareOnTrackPoints()) {
                pur.setOnTrack(false);
                saveOnTrackItems(createOnTrackTransactionItem(pur.getUser(), pur.getRound(), myShareOnTrackName + "  revert", -10), myShareOnTrackName + "  revert");
                pur.setMyShareOnTrackPoints(false);
            }
        }
        Double sumPoints = transactionItemService.sumPointsByUserAndRound(pur.getUser(), pur.getRound());
        pur.addRoundCoins(sumPoints == null ? 0 : sumPoints);

        Double userPoints =transactionItemService.sumPointsByUserAndSeasonYear(pur.getUser(), pur.getRound().getSeason().getSeasonYear());
//        Double userPoints = sumByUserAndSeason(pur.getUser(), pur.getRound().getSeason().getSeasonYear());

        pur.getUser().setPoints(userPoints == null ? 0 : userPoints);
        userService.save(pur.getUser());

    }

    @Override
    protected JpaRepository<PaceUserRound, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<PaceUserRound> getEntityClass() {
        return PaceUserRound.class;
    }

    @Override
    public PaceUserRound createEntity() {
        return new PaceUserRound();
    }

    @Scheduled(cron = "0 0 17 * * TUE")
    private void sendOnTrackEmails() {
        Round currentRound = roundService.getLastRound();
        for (UserStatus u : userStatusService.fetchByQuery(LocalDate.now().getYear(), null)) {
            Optional<PaceUserRound> ur = repository.findByUserAndRound(u.getUser(), currentRound);
            Integer goal = u.getGoal();
            if (ur.isPresent()) {
                if (!ur.get().isOnTrack() && !u.getUser().getEmail().isEmpty()) {
                    try {
//                        if (u.getId() == 255) {
                        microsoftService.sendStatusUpdate(u.getTransactions(), u.getStatus() * 100, ur.get().getRoundMyShareGoal() - u.getTransactions(), u.getUser(), currentRound);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void saveOnTrackItems(TransactionItem transactionItem, String description) {
        Optional<Transaction> onTrackTransaction = transactionService.findByName(description);
        if (onTrackTransaction.isEmpty()) {
            Transaction newTransaction = new Transaction();
            newTransaction.setName(description);
            newTransaction.setAccount(Account.OTHER);
            newTransaction.setCreateDateTime(LocalDateTime.now());
            newTransaction.setCreateUser(userService.findAllByRole(Role.ADMIN).iterator().next());
            Transaction savedTransaction = transactionService.save(newTransaction);

            transactionItem.setTransactionId(savedTransaction.getId());
        } else {
            transactionItem.setTransactionId(onTrackTransaction.get().getId());
        }

        transactionItemService.save(transactionItem);
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
}
