package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Goal;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByUserAndSeasonSeasonYear(User user, Integer season);

    List<Goal> findBySeason(Season season);
}
