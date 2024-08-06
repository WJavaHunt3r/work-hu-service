package com.ktk.workhuservice.data.activityitems;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityItemRepository extends JpaRepository<ActivityItem, Long> {

    List<ActivityItem> findByActivity_Id(Long activityId);

    @Query("SELECT ai from ActivityItem ai where " +
            " ( ?1 is NULL OR ai.user = ?1 ) " +
            " and (?2 is null or ai.activity.registeredInApp = ?2 ) " )
    List<ActivityItem> fetchByQuery(Long userId, Boolean registeredInApp, Long roundId);

    @Query("select sum(ai.hours) from ActivityItem ai where ai.activity.id = ?1 ")
    double sumHoursByActivity(Long activityId);
}
