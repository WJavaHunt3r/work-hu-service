package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {

    List<Camp> findAllBySeason(Season season);
}
