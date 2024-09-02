package com.ktk.workhuservice.data.users;

import com.ktk.workhuservice.data.camps.Camp;
import com.ktk.workhuservice.data.camps.CampService;
import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.teams.Team;
import com.ktk.workhuservice.data.transactionitems.TransactionItem;
import com.ktk.workhuservice.data.transactionitems.TransactionItemService;
import com.ktk.workhuservice.data.usercamps.UserCamp;
import com.ktk.workhuservice.data.usercamps.UserCampService;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService extends BaseService<User, Long> {

    private UserRepository userRepository;
    private TransactionItemService transactionItemService;
    private UserCampService userCampService;
    private CampService campService;

    public UserService(UserRepository userRepository, TransactionItemService transactionService, UserCampService userCampService, CampService campService) {
        this.userRepository = userRepository;
        this.transactionItemService = transactionService;
        this.userCampService = userCampService;
        this.campService = campService;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(this::calculateUserPoints);
        return user;
    }

    public Iterable<User> getYouth() {
        return getYouth(LocalDate.now().getYear());
    }

    public Iterable<User> getYouth(int year) {
        return userRepository.findAllBUKBySeason(year);
    }

    public void calculateUserPointsForAllUsers() {
        getYouth().forEach(this::calculateUserPoints);
    }

    public Iterable<User> findAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public Iterable<User> findAllByTeam(Team t, Season s) {
        return userRepository.findAllByTeamAndSeasonAndGoal(t, s);
    }

    public Iterable<User> findAllByPaceTeam(PaceTeam t, Season s) {
        return userRepository.findAllByPaceTeamAndSeasonAndGoal(t, s);
    }

    public Optional<User> findByMyShareId(Long id) {
        return userRepository.findByMyShareID(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Long countAllByTeam(Team t, Integer season) {
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

    public void setPaceTeams(PaceTeam bukTeam, PaceTeam samvirkTeam) {
        Camp paskeCamp = campService.createPaskeCamp();
        getYouth().forEach(u -> {
            if(u.getTeam() == null) {
                int age = u.getAgeAtDate(LocalDate.of(2024, 12, 31));
                if (18 <= age && age < 26) {
                    u.setPaceTeam(samvirkTeam);
                } else {
                    u.setPaceTeam(bukTeam);
                }

                if (userCampService.fetchByQuery(u, paskeCamp, paskeCamp.getSeason(), null).isEmpty()) {

                    UserCamp paskeUserCamp = userCampService.createEntity();
                    paskeUserCamp.setUser(u);
                    paskeUserCamp.setCamp(paskeCamp);
                    paskeUserCamp.setParticipates(true);
                    if (age < 18) {
                        paskeUserCamp.setPrice(paskeCamp.getU18BrunstadFee() + paskeCamp.getU18LocalFee());
                    } else {
                        paskeUserCamp.setPrice(paskeCamp.getO18BrunstadFee() + paskeCamp.getO18LocalFee());
                    }

                    userCampService.save(paskeUserCamp);
                }
                save(u);
            }
        });

    }
}
