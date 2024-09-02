package com.ktk.workhuservice.data.paceuserround;

import com.ktk.workhuservice.data.camps.Camp;
import com.ktk.workhuservice.data.goals.Goal;
import com.ktk.workhuservice.data.goals.GoalService;
import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.seasons.SeasonService;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.usercamps.UserCamp;
import com.ktk.workhuservice.data.usercamps.UserCampService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaceUserRoundService extends BaseService<PaceUserRound, Long> {
    private PaceUserRoundRepository repository;
    private UserService userService;
    private GoalService goalService;
    private UserCampService userCampService;
    private SeasonService seasonService;
    private RoundService roundService;
    private TransactionItemService transactionItemService;

    public PaceUserRoundService(PaceUserRoundRepository repository, UserService userService, GoalService goalService, UserCampService userCampService, SeasonService seasonService, RoundService roundService, TransactionItemService transactionItemService) {
        this.repository = repository;
        this.userService = userService;
        this.goalService = goalService;
        this.userCampService = userCampService;
        this.seasonService = seasonService;
        this.roundService = roundService;
        this.transactionItemService = transactionItemService;
    }

    public List<PaceUserRound> findByUser(User u) {
        return repository.findByUser(u);
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

    public void calculateAllUserRoundStatus() {
        calculateAllUserRoundStatus(roundService.getLastRound());
    }

    public void createAllPaceUserRounds() {
        createAllPaceUserRounds(roundService.getLastRound());
    }

    public void createAllPaceUserRounds(Round round) {
        userService.calculateUserPointsForAllUsers();
        for (User u : userService.getYouth()) {
            Optional<PaceUserRound> pur = findByUserAndRound(u, round);
            if (pur.isPresent()) {
                pur.get().setRoundMyShareGoal(calculateCurrRoundMyShareGoal(round, u));
            } else {
                save(createPaceUserRound(u, round));
            }
        }
    }

    public void calculateAllUserRoundStatus(Round round) {
        userService.calculateUserPointsForAllUsers();
        for (User u : userService.getYouth()) {
            calculateUserRoundStatus(round, u);
        }
    }

    public void calculateUserRoundStatus(Round r, User u) {
        findByUserAndRound(u, r).ifPresent(pur -> {
            calculateUserRoundStatus(pur);
            save(pur);
        });
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
        Optional<Goal> goal = goalService.findByUserAndSeasonYear(u, round.getSeason().getSeasonYear());
        if (goal.isPresent()) {
            Optional<Season> nextSeason = seasonService.findBySeasonYear(round.getSeason().getSeasonYear() + 1);
            int sumGoal = goal.get().getGoal();
            if (nextSeason.isPresent()) {
                List<UserCamp> userCamps = userCampService.fetchByQuery(u, null, nextSeason.get(), true);
                if (!userCamps.isEmpty()) {
                    Camp camp = userCamps.get(0).getCamp();
                    if (camp.getCampDate().compareTo(LocalDate.of(2025, 5, 1)) < 0) {
                        return (sumGoal + userCamps.get(0).getPrice() - u.getCurrentMyShareCredit()) / (camp.getCampDate().getMonthValue() - 1 + 12 - LocalDate.now().getMonthValue() + 1);
                    }
                }

            }
            return (sumGoal - u.getCurrentMyShareCredit()) / (12 - LocalDate.now().getMonthValue() + 1);
        }
        return 0;
    }

    private void calculateUserRoundStatus(PaceUserRound pur) {
        pur.setRoundCoins(0);
        Optional<Goal> goal = goalService.findByUserAndSeasonYear(pur.getUser(), pur.getRound().getSeason().getSeasonYear());
        if (goal.isPresent() && (double) pur.getUser().getCurrentMyShareCredit() / (double) goal.get().getGoal() * 100 >= pur.getRound().getMyShareGoal()) {
            pur.setRoundCoins(50);
        }
        Integer sumCredits = transactionItemService.sumCreditByUserAndRound(pur.getUser(), pur.getRound());
        pur.setRoundCredits(sumCredits != null ? sumCredits : 0);
        if (pur.getRoundCredits() >= pur.getRoundMyShareGoal()) {
            pur.setRoundCoins(100);
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

    public List<PaceUserRound> findByUserAndSeasonYear(User user, Integer seasonYear) {
        return repository.findByUserAndSeason(user, seasonYear);
    }
}
