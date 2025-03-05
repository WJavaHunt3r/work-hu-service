package com.ktk.workhuservice.data.paceteamround;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.paceteam.PaceTeamService;
import com.ktk.workhuservice.data.paceuserround.PaceUserRoundService;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.seasons.SeasonService;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PaceTeamRoundService extends BaseService<PaceTeamRound, Long> {
    private final PaceTeamRoundRepository repository;
    private final PaceTeamService paceTeamService;
    private final RoundService roundService;
    private final PaceUserRoundService paceUserRoundService;
    private final SeasonService seasonService;

    public PaceTeamRoundService(PaceTeamRoundRepository repository, PaceTeamService paceTeamService, RoundService roundService, PaceUserRoundService paceUserRoundService, SeasonService seasonService) {
        this.repository = repository;
        this.paceTeamService = paceTeamService;
        this.roundService = roundService;
        this.paceUserRoundService = paceUserRoundService;
        this.seasonService = seasonService;
    }

    public Iterable<PaceTeamRound> findAllActiveTeamRounds(int seasonYear) {
        return findAllActiveTeamRounds(seasonYear, null);
    }

    public Iterable<PaceTeamRound> findAllActiveTeamRounds(int seasonYear, PaceTeam team) {
        return repository.findAllActiveBySeasonYear(seasonYear, team);
    }

    public void calculateAllTeamRoundPoints() {
        calculateAllTeamRoundPoints(roundService.getLastRound());
    }

    private Double sumTeamPoints(PaceTeam team) {
        return repository.sumTeamPoints(LocalDate.now().getYear(), team);
    }

    public void calculateAllTeamRoundPoints(Round round) {
        for (PaceTeamRound pct : repository.findAllByRound(round)) {
            calculateRoundPointsForTeam(pct);
        }
        for (PaceTeam team : paceTeamService.findActiveTeams(true)) {
            Double points = sumTeamPoints(team);
            team.setCoins(points == null ? 0 : points);
            paceTeamService.save(team);
        }
    }

    private void calculateRoundPointsForTeam(PaceTeamRound ptr) {
        paceUserRoundService.createAllPaceUserRounds(ptr.getRound());
        Integer sumCoins = paceUserRoundService.calculatePaceTeamRoundCoins(ptr.getTeam(), ptr.getRound());
        int teamMemberCount = paceUserRoundService.countByRoundAndTeam(ptr.getRound(), ptr.getTeam());
        ptr.setMaxTeamRoundCoins(teamMemberCount);
        ptr.setTeamRoundCoins(sumCoins == null ? 0 : sumCoins);
        ptr.setTeamRoundStatus(sumCoins == null ? 0 : (double) sumCoins / (double) teamMemberCount);
        save(ptr);
    }

    private PaceTeamRound createTeamRound(PaceTeam t, Round r) {
        PaceTeamRound teamRound = new PaceTeamRound();
        teamRound.setTeam(t);
        teamRound.setRound(r);
        return save(teamRound);
    }

    @Override
    protected JpaRepository<PaceTeamRound, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<PaceTeamRound> getEntityClass() {
        return PaceTeamRound.class;
    }

    @Override
    public PaceTeamRound createEntity() {
        return new PaceTeamRound();
    }

    //    @Scheduled(cron = "0 1 0 1 * ?")
    @Scheduled(cron = "0 1 0 * * SUN")
    public void createTeamRounds() {
        Season season = seasonService.findBySeasonYear(LocalDate.now().getYear()).orElseGet(() -> seasonService.createSeasonForYear(LocalDate.now().getYear()));
        Round currRound = roundService.findRoundByDate(LocalDateTime.now()).orElseGet(() -> roundService.createNextRound(season));
        paceTeamService.findActiveTeams().forEach(paceTeam -> {
            paceUserRoundService.createAllPaceUserRounds(currRound);
            calculateRoundPointsForTeam(repository.findByTeamAndRound(paceTeam, currRound).orElseGet(() -> createTeamRound(paceTeam, currRound)));
            roundService.save(currRound);
        });
    }

}
