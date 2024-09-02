package com.ktk.workhuservice.data.paceteamround;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.paceteam.PaceTeamService;
import com.ktk.workhuservice.data.paceuserround.PaceUserRoundService;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.rounds.RoundService;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaceTeamRoundService extends BaseService<PaceTeamRound, Long> {
    private PaceTeamRoundRepository repository;
    private PaceTeamService paceTeamService;
    private RoundService roundService;
    private PaceUserRoundService paceUserRoundService;

    public PaceTeamRoundService(PaceTeamRoundRepository repository, PaceTeamService paceTeamService, RoundService roundService, PaceUserRoundService paceUserRoundService) {
        this.repository = repository;
        this.paceTeamService = paceTeamService;
        this.roundService = roundService;
        this.paceUserRoundService = paceUserRoundService;
    }

    public Iterable<PaceTeamRound> findAll(int seasonYear) {
        return repository.findAllBySeasonYear(seasonYear);
    }

    public void calculateAllTeamRoundPoints() {
        calculateAllTeamRoundPoints(roundService.getLastRound());
    }

    public void calculateAllTeamRoundPoints(Round round) {
        for (PaceTeamRound pct : repository.findAllByRound(round)) {
            paceUserRoundService.calculateAllUserRoundStatus(round);
            calculateRoundCoinsForTeam(pct);
        }
    }

    private void calculateRoundCoinsForTeam(PaceTeamRound ptr) {
        int sumCoins = paceUserRoundService.calculatePaceTeamRoundCoins(ptr.getTeam(), ptr.getRound());
        int maxCoins = paceUserRoundService.countByRoundAndTeam(ptr.getRound(), ptr.getTeam()) * 100;
        ptr.setMaxTeamRoundCoins(maxCoins);
        ptr.setTeamRoundCoins(sumCoins);
        ptr.setTeamRoundStatus((double) sumCoins / (double) maxCoins);
        save(ptr);
    }

    public void createAllTeamRounds(){
        createTeamRounds();
    }
    private PaceTeamRound createTeamRound(PaceTeam t, Round r) {
        PaceTeamRound teamRound = new PaceTeamRound();
        teamRound.setTeam(t);
        teamRound.setRound(r);
        return save(teamRound);
    }

    private Optional<PaceTeamRound> findByTeamAndRound(PaceTeam team, Round round) {
        return repository.findByTeamAndRound(team, round);
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

    @Scheduled(cron = "0 1 0 1 * ?")
    public void createTeamRounds() {
        Optional<Round> currRound = roundService.findRoundByDate(LocalDateTime.now());
        currRound.ifPresent(round -> paceTeamService.findAll().forEach(paceTeam -> {
            paceUserRoundService.createAllPaceUserRounds(currRound.get());
            calculateRoundCoinsForTeam(repository.findByTeamAndRound(paceTeam, round).orElse(createTeamRound(paceTeam, round)));

        }));
    }

}
