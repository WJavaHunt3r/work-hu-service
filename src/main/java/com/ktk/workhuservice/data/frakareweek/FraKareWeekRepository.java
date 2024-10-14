package com.ktk.workhuservice.data.frakareweek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraKareWeekRepository extends JpaRepository<FraKareWeek, Long> {
    @Query(" SELECT fks from FraKareWeek fks where ( ?1 is null or fks.year = ?1 ) " +
            " and ( ?2 is null or fks.weekNumber = ?2 ) order by fks.weekNumber desc ")
    List<FraKareWeek> fetchByQuery(Integer year, Integer weekNumber);

}
