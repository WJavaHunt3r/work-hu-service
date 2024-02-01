package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.ActivityItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityItemRepository extends JpaRepository<ActivityItem, Long> {

    List<ActivityItem> findByActivity_Id(Long activityId);
}
