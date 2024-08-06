package com.ktk.workhuservice.data.paceteam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaceTeamRepository extends JpaRepository<PaceTeam, Long> {
}
