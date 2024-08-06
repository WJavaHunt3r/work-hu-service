package com.ktk.workhuservice.data.usercamps;

import com.ktk.workhuservice.data.camps.Camp;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCampRepository extends JpaRepository<UserCamp, Long> {

    List<UserCamp> findAllByUser(User user);

    @Query("SELECT uc From UserCamp uc JOIN FETCH uc.camp JOIN FETCH uc.user WHERE ( ?1 is NULL or uc.user = ?1 )" +
            " AND ( ?2 IS NULL or uc.camp = ?2 )" +
            " AND (uc.camp.season = ?3 or ?3 IS NULL ) " +
            " AND (uc.participates = ?4 or ?4 IS NULL )")
    List<UserCamp> fetchByQuery(User user, Camp camp, Season season, Boolean participates);

}
