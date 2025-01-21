package com.ktk.workhuservice.data.camps;

import com.ktk.workhuservice.data.seasons.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {

    List<Camp> findAllBySeason(Season season);

    Optional<Camp> findByCampName(String name);
}
