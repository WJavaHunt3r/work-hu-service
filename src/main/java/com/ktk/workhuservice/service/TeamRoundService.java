package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.TeamRound;
import com.ktk.workhuservice.data.UserRound;
import com.ktk.workhuservice.repositories.TeamRoundRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamRoundService extends BaseService<TeamRound, Long> {
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

    public Iterable<TeamRound> findAll(int seasonYear) {
        teamService.findAll().forEach(t ->
                roundService.findAllBySeasonYear(seasonYear).forEach(r -> {
                            if (findByTeamAndRound(t, r).isEmpty()) {
                                save(createTeamRound(t, r));
                            }
                        }
                )
        );
        calculateTeamRoundPoints();
        return teamRoundRepository.findAllBySeasonYear(seasonYear);
    }

    private void calculateTeamRoundPoints() {
        for (TeamRound tr : teamRoundRepository.findAllByRound(roundService.getLastRound())) {

            double teamPoints = 0;
            double samvirkPayments = 0;
            for (UserRound ur : userRoundService.findByRoundAndTeam(tr.getRound(), tr.getTeam())) {
                samvirkPayments += ur.getSamvirkPayments();
                teamPoints += ur.getRoundPoints();

            }
            tr.setSamvirkPayments(samvirkPayments);
            tr.setTeamPoints(teamPoints / userService.countAllByTeam(tr.getTeam(), tr.getRound().getSeason().getSeasonYear()));
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

    @Override
    protected JpaRepository<TeamRound, Long> getRepository() {
        return teamRoundRepository;
    }

    @Override
    public Class<TeamRound> getEntityClass() {
        return TeamRound.class;
    }

    @Override
    public TeamRound createEntity() {
        return new TeamRound();
    }
}
