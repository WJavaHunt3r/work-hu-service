package com.ktk.workhuservice.data.userstatus;

import com.ktk.workhuservice.data.goals.Goal;
import com.ktk.workhuservice.data.goals.GoalService;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.seasons.SeasonService;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserStatusService extends BaseService<UserStatus, Long> {

    private final UserStatusRepository repository;
    private final GoalService goalService;
    private final TransactionItemService transactionItemService;
    private final SeasonService seasonService;

    public UserStatusService(UserStatusRepository repository, GoalService goalService, TransactionItemService transactionItemService, SeasonService seasonService) {
        this.repository = repository;
        this.goalService = goalService;
        this.transactionItemService = transactionItemService;
        this.seasonService = seasonService;
    }

    public Optional<UserStatus> findByUserId(Long userId, Integer seasonYear) {
        return repository.findByUserIdAndSeasonYear(userId, seasonYear);
    }

    public Optional<UserStatus> findByUserId(Long userId) {
        return repository.findByUserIdAndSeasonYear(userId, seasonService.findCurrentSeason().getSeasonYear());
    }

    public List<UserStatus> fetchByQuery(Integer seasonYear, Long teamId) {
        return repository.fetchByQuery(seasonYear,teamId);
    }

    public void createUserStatusForAllUsers(Integer seasonYear) {
        Optional<Season> season = seasonService.findBySeasonYear(seasonYear);
        if (season.isEmpty()) {
            return;
        }
        for (Goal goal : goalService.findBySeason(season.get())) {
            createUserStatus(goal.getUser(), goal.getGoal(), season.get());
        }
    }

    void createUserStatus(User u, Integer goal, Season season) {
        UserStatus status = createEntity();
        status.setUser(u);
        status.setGoal(goal);
        status.setSeason(season);
        calculateUserStatus(status);
        save(status);
    }

    public void calculateUserStatus(User u) {
        findByUserId(u.getId()).ifPresent(this::calculateUserStatus);
    }

    public void calculateUserStatus(UserStatus us) {
        Integer transactions = 0;
        Integer sumCredit = transactionItemService.sumCreditByUserAndSeasonYear(us.getUser(), us.getSeason().getSeasonYear(), Account.MYSHARE);
        if(sumCredit != null){
            transactions += sumCredit;
        }
        Optional<UserStatus> lastYearStatus = findByUserId(us.getUser().getId(), us.getSeason().getSeasonYear() - 1);
        if (lastYearStatus.isPresent()) {
            int transition = Math.max(lastYearStatus.get().getTransition(), 0);
            transactions += transition;
        } else {
            transactions += us.getUser().getBaseMyShareCredit();
        }
        us.setStatus((double) transactions / us.getGoal());
        us.setTransactions(transactions);
        us.setTransition(Math.max(us.getTransactions() - us.getGoal(), 0));
    }

    @Override
    protected JpaRepository<UserStatus, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<UserStatus> getEntityClass() {
        return UserStatus.class;
    }

    @Override
    public UserStatus createEntity() {
        return new UserStatus();
    }
}
