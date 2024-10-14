package com.ktk.workhuservice.data.userfrakarestreak;

import com.ktk.workhuservice.data.frakareweek.FraKareWeek;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.users.UserService;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFraKareWeekService extends BaseService<UserFraKareWeek, Long> {

    private UserFraKareWeekRepository repository;
    private UserService userService;

    public UserFraKareWeekService(UserFraKareWeekRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    protected JpaRepository<UserFraKareWeek, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<UserFraKareWeek> getEntityClass() {
        return UserFraKareWeek.class;
    }

    @Override
    public UserFraKareWeek createEntity() {
        return new UserFraKareWeek();
    }

    public List<UserFraKareWeek> fetchByQuery(Long userId, Integer weekNumber, Boolean listened, Long teamId) {
        return repository.fetchByQuery(userId, weekNumber, listened, teamId);
    }

    public void createUserFraKareWeeks(FraKareWeek week) {
        for (User u : userService.getYouth()) {
            if (fetchByQuery(u.getId(), week.getWeekNumber(), null, null).isEmpty()) {
                UserFraKareWeek streak = createEntity();
                streak.setUser(u);
                streak.setListened(false);
                streak.setFraKareWeek(week);
                save(streak);
            }
        }
    }
}
