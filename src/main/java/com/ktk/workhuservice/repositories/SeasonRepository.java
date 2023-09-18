package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findBySeasonNumber(Integer seasonNumber);

    @Query("SELECT s FROM Season s WHERE s.startDateTime < ?1 AND s.endDateTime > ?1 " )
    Optional<Season> findCurrentSeason(LocalDateTime now);
}
