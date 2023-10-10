package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.data.UserRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoundRepository extends JpaRepository<UserRound, Long> {

    Optional<UserRound> findByUserAndRound(User u, Round r);

    Iterable<UserRound> findByUser(User u);

    Iterable<UserRound> findByRound(Round r);
}
