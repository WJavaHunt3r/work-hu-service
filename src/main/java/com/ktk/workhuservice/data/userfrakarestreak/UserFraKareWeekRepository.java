package com.ktk.workhuservice.data.userfrakarestreak;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFraKareWeekRepository extends JpaRepository<UserFraKareWeek, Long> {
    @Query(" SELECT fks from UserFraKareWeek fks where ( ?1 is null or fks.user.id = ?1 ) " +
            " and ( ?2 is null or fks.fraKareWeek.weekNumber = ?2 ) " +
            " and ( ?3 is null or fks.listened = ?3 ) " +
            " and ( ?4 is null or fks.user.paceTeam is null or fks.user.paceTeam.id = ?4 ) " +
            " order by fks.user.lastname, fks.user.firstname ")
    List<UserFraKareWeek> fetchByQuery(Long userId, Integer weekNumber, Boolean listened, Long teamId);

}
