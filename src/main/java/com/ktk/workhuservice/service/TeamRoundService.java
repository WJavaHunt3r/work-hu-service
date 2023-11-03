package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.*;
import com.ktk.workhuservice.repositories.TeamRoundRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        calculateTeamRoundPoints();
        return teamRoundRepository.findAll();
    }

    private void calculateTeamRoundPoints() {
        for (TeamRound tr : teamRoundRepository.findAll()) {
            double teamPoints = 0;
            double samvirkPayments = 0;
            for (UserRound ur : userRoundService.findByRoundAndTeam(tr.getRound(), tr.getTeam())) {
                    samvirkPayments += ur.getSamvirkPayments();
                    teamPoints+= ur.getRoundPoints();

            }
            tr.setSamvirkPayments(samvirkPayments);
            tr.setTeamPoints(teamPoints / userService.countAllByTeam(tr.getTeam()));
            teamRoundRepository.save(tr);
        }

    }

    private TeamRound createTeamRound(Team t, Round r) {
        TeamRound teamRound = new TeamRound();
        teamRound.setTeam(t);
        teamRound.setRound(r);
        return teamRound;
    }

    private Optional<TeamRound> findByTeamAndRound(Team team, Round round) {
        return teamRoundRepository.findByTeamAndRound(team, round);
    }

    public Iterable<TeamRound> findAllByRound(Round round) {
        return teamRoundRepository.findAllByRound(round);
    }

}
