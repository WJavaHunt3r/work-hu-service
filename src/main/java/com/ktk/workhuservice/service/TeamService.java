package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.enums.TeamColor;
import com.ktk.workhuservice.repositories.TeamRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService extends BaseService<Team, Long>{
    private TeamRepository teamRepository;
    private UserService userService;

    public TeamService(TeamRepository teamRepository, UserService userService, UserRoundService userRoundService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    public Optional<Team> findByColor(TeamColor c) {
        return teamRepository.findTeamByColor(c);
    }

    public void recalculateAllTeamPoints(){
        userService.calculateUserPointsForAllUsers();
    }

    @Override
    protected JpaRepository<Team, Long> getRepository() {
        return teamRepository;
    }

    @Override
    public Class<Team> getEntityClass() {
        return Team.class;
    }

    @Override
    public Team createEntity() {
        return new Team();
    }
}
