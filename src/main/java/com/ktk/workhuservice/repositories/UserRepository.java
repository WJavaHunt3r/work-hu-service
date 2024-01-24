package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByMyShareID(Long id);

    Iterable<User> findAllByRole(Role role);

    @Query("SELECT u FROM Goal g JOIN g.user u JOIN g.season s where s = ?2 and u.team = ?1")
    Iterable<User> findAllByTeamAndSeasonAndGoal(Team t, Season s);

    @Query("SELECT COUNT(u) FROM Goal g JOIN g.user u JOIN g.season s where s.seasonYear = ?2 and u.team = ?1")
    Long countAllByTeamAndSeasonAndGoal(Team t, Integer s);

    @Query("SELECT u FROM Goal g JOIN g.user u JOIN g.season s where s.seasonYear = ?1 and u IS NOT null ")
    Iterable<User> findAllBUKBySeason(Integer seasonYear);
}
