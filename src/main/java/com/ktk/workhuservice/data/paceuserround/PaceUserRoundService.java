package com.ktk.workhuservice.data.paceuserround;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.userstatus.UserStatus;
import com.ktk.workhuservice.data.userstatus.UserStatusService;
import com.ktk.workhuservice.service.BaseService;
import com.ktk.workhuservice.service.microsoft.MicrosoftService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaceUserRoundService extends BaseService<PaceUserRound, Long> {
    private final PaceUserRoundRepository repository;
    private final TransactionItemService transactionItemService;
    private final RoundService roundService;
    private final MicrosoftService microsoftService;
    private final UserStatusService userStatusService;

    public PaceUserRoundService(PaceUserRoundRepository repository, TransactionItemService transactionItemService, RoundService roundService, MicrosoftService microsoftService, UserStatusService userStatusService) {
        this.repository = repository;
        this.transactionItemService = transactionItemService;
        this.roundService = roundService;
        this.microsoftService = microsoftService;
        this.userStatusService = userStatusService;
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

    public int calculatePaceTeamRoundCoins(PaceTeam t, Round round) {
        return repository.calculatePaceTeamRoundCoins(t, round);
    }

    public void createAllPaceUserRounds(Round round) {
        for (UserStatus us : userStatusService.fetchByQuery(LocalDate.now().getYear())) {
            Optional<PaceUserRound> pur = findByUserAndRound(us.getUser(), round);
            if (pur.isPresent()) {
                pur.get().setRoundMyShareGoal(calculateCurrRoundMyShareGoal(round, us.getUser()));
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
        pur.setRoundMyShareGoal(calculateCurrRoundMyShareGoal(round, u));
        pur.setRoundCredits(0);
        pur.setRoundCoins(0);
        calculateUserRoundStatus(pur);

        return pur;
    }

    private int calculateCurrRoundMyShareGoal(Round round, User u) {
        Optional<UserStatus> us = userStatusService.findByUserId(u.getId(), round.getSeason().getSeasonYear());
        return us.map(userStatus -> userStatus.getGoal() * (round.getMyShareGoal() / 100)).orElse(0);
    }

    private void calculateUserRoundStatus(PaceUserRound pur) {
        pur.setRoundMyShareGoal(calculateCurrRoundMyShareGoal(pur.getRound(), pur.getUser()));
        pur.setRoundCoins(0);
        Optional<UserStatus> us = userStatusService.findByUserId(pur.getUser().getId(), pur.getRound().getSeason().getSeasonYear());
        if (us.isPresent() && (double) us.get().getTransactions() / (double) us.get().getGoal() * 100 >= pur.getRound().getMyShareGoal()) {
            pur.setRoundCoins(50);
        }
        Integer sumCredits = transactionItemService.sumCreditByUserAndRound(pur.getUser(), pur.getRound());
        pur.setRoundCredits(sumCredits != null ? sumCredits : 0);
        if (pur.getRoundCredits() >= pur.getRoundMyShareGoal()) {
            pur.setRoundCoins(pur.getRoundCoins() + 50);
        }
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
        for (UserStatus u : userStatusService.fetchByQuery(LocalDate.now().getYear())) {
            Optional<PaceUserRound> ur = repository.findByUserAndRound(u.getUser(), currentRound);
            Integer goal = u.getGoal();
            if (ur.isPresent()) {
                double currentUserGoal = goal * (currentRound.getMyShareGoal() / 100.0);
                if (u.getTransactions() < currentUserGoal && !u.getUser().getEmail().isEmpty()) {
                    try {
//                        if (u.getId() == 255) {
                        microsoftService.sendStatusUpdate(u.getTransactions(), (double) u.getTransactions() / goal * 100, (int) currentUserGoal - u.getTransactions(), u.getUser(), currentRound);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
