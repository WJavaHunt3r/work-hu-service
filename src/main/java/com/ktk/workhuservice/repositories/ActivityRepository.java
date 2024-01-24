package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT ac FROM Activity ac WHERE (ac.responsible.id = ?1 OR ?1 IS NULL) " +
            " AND (ac.employer.id = ?2 OR ?2 IS NULL) " +
            " AND (ac.paid = ?3 OR ?3 IS NULL) " +
            " AND (ac.registeredInApp = ?4 OR ?4 IS NULL) " +
            " AND (ac.registeredInMyShare = ?5 OR ?5 IS NULL) " +
            " AND (ac.createUser.id = ?6 OR ?6 IS NULL)")
    List<Activity> fetchByQuery(Long responsible, Long employer, boolean paid, boolean registeredInApp, boolean registeredInMyShare, Long createUser);
}
