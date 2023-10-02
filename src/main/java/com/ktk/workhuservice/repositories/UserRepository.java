package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByMyShareID(Long id);

    Iterable<User> findAllByRole(Role role);

    Iterable<User> findAllByTeam(Team t);

    int countAllByTeamAndGoalGreaterThan(Team t, int goal);

    Iterable<User> findAllByGoalGreaterThan(int goal);
}
