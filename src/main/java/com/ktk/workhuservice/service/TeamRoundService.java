package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.TeamRound;
import com.ktk.workhuservice.repositories.TeamRoundRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TeamRoundService {
    private TeamRoundRepository teamRoundRepository;
    private RoundService roundService;
    private TeamService teamService;
    private UserService userService;
    private UserRoundService userRoundService;

    public TeamRoundService(TeamRoundRepository teamRoundRepository, RoundService userRoundService, TeamService teamService, UserService userService, UserRoundService userRoundService1) {
        this.teamRoundRepository = teamRoundRepository;
        this.roundService = userRoundService;
        this.teamService = teamService;
        this.userService = userService;
        this.userRoundService = userRoundService1;
    }

    public TeamRound save(TeamRound teamRound) {
        return teamRoundRepository.save(teamRound);
    }

    public Iterable<TeamRound> findAll() {
        teamService.getAll().forEach(t ->
                roundService.getAll().forEach(r -> {
                            if (findByTeamAndRound(t, r).isEmpty()) {
                                save(createTeamRound(t, r));
                            }
                        }
                )
        );
        teamRoundRepository.findAll().forEach(tr ->
                tr.setTeamPoints(calculateTeamRoundPoints(tr.getTeam(), tr.getRound()) / userService.countAllByTeam(tr.getTeam())));
        return teamRoundRepository.findAll();
    }

    private double calculateTeamRoundPoints(Team team, Round round) {
        return StreamSupport.stream(userService.findAllByTeam(team).spliterator(), false)
                .mapToDouble(u -> userRoundService.findByUserAndRound(u, round).getRoundPoints()).sum();
    }

    private TeamRound createTeamRound(Team t, Round r) {
        TeamRound teamRound = new TeamRound();
        teamRound.setTeam(t);
        teamRound.setRound(r);
        return teamRound;
    }

    public Optional<TeamRound> findByTeamAndRound(Team team, Round round) {
        return teamRoundRepository.findByTeamAndRound(team, round);
    }

    public Iterable<TeamRound> findAllByRound(Round round) {
        return teamRoundRepository.findAllByRound(round);
    }

}
