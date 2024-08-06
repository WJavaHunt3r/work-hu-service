package com.ktk.workhuservice.data.paceuserround;

import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaceUserRoundRepository extends JpaRepository<PaceUserRound, Long> {
    Optional<PaceUserRound> findByUserAndRound(User u, Round r);

    List<PaceUserRound> findByUser(User u);

    @Query("SELECT ur FROM PaceUserRound ur WHERE ur.user = ?1 AND ur.round.season.seasonYear = ?2 ")
    List<PaceUserRound> findByUserAndSeason(User u, Integer seasonYear);

    Iterable<PaceUserRound> findByRound(Round r);

    @Query("SELECT ur FROM PaceUserRound ur WHERE ur.round = ?1 AND ur.user.paceTeam = ?2 ")
    Iterable<PaceUserRound> findByRoundAndTeam(Round r, PaceTeam t);

    @Query("SELECT COUNT(ur) FROM PaceUserRound ur WHERE ur.round = ?1 AND ur.user.paceTeam = ?2 ")
    int countByRoundAndTeam(Round r, PaceTeam t);

    @Query("SELECT sum(ur.roundCoins) FROM PaceUserRound  ur where ur.user.paceTeam = ?1 " +
            " and ur.round = ?2 ")
    Integer calculatePaceTeamRoundCoins(PaceTeam t, Round round);

    @Query("SELECT sum(ur.samvirkPayments) FROM PaceUserRound  ur where ur.user.paceTeam = ?1 " +
            " and ur.round = ?2 ")
    double calculateTeamRoundSamvirkPayments(PaceTeam t, Round round);
}
