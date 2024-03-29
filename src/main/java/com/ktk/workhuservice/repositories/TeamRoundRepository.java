package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.TeamRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRoundRepository extends JpaRepository<TeamRound, Long> {
    Optional<TeamRound> findByTeamAndRound(Team team, Round round);

    Iterable<TeamRound> findAllByTeam(Team team);

    Iterable<TeamRound> findAllByRound(Round round);

    @Query("SELECT tr from TeamRound tr where round.season.seasonYear = ?1")
    Iterable<TeamRound> findAllBySeasonYear(Integer seasonYear);
}
