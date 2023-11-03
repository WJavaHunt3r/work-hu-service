package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.data.UserRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoundRepository extends JpaRepository<UserRound, Long> {

    Optional<UserRound> findByUserAndRound(User u, Round r);

    Iterable<UserRound> findByUser(User u);

    Iterable<UserRound> findByRound(Round r);

    @Query("SELECT ur FROM UserRound ur WHERE ur.round = ?1 AND ur.user.team = ?2 ")
    Iterable<UserRound> findByRoundAndTeam(Round r, Team t);
}
