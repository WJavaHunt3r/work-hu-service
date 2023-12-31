package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.TeamColor;
import com.ktk.workhuservice.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {
    private TeamRepository teamRepository;
    private UserService userService;
    private UserRoundService userRoundService;

    public TeamService(TeamRepository teamRepository, UserService userService, UserRoundService userRoundService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.userRoundService = userRoundService;
    }

    public Iterable<Team> getAll() {
        recalculateAllTeamPoints();
        return teamRepository.findAll();
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public Team save(Team t) {
        return teamRepository.save(t);
    }

    public Optional<Team> findByColor(TeamColor c) {
        return teamRepository.findTeamByColor(c);
    }

    public void deleteAll() {
        teamRepository.deleteAll();
    }

    public Optional<Team> findByMyShareId(Long id){
        return teamRepository.findTeamByTeamLeaderMyShareId(id);
    }

    private void recalculatePointsForAllRounds(Team t){
        double points = 0;
        for (User u : userService.findAllByTeam(t)){
            points += u.getPoints();
        }
        t.setPoints(points/userService.countAllByTeam(t));
        teamRepository.save(t);
    }

    public void recalculateAllTeamPoints(){
        userService.calculateUserPointsForAllUsers();
        teamRepository.findAll().iterator().forEachRemaining(this::recalculatePointsForAllRounds);
    }
}
