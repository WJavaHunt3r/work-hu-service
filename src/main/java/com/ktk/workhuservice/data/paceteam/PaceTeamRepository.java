package com.ktk.workhuservice.data.paceteam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaceTeamRepository extends JpaRepository<PaceTeam, Long> {

    Optional<PaceTeam> findByTeamName(String teamName);
}
