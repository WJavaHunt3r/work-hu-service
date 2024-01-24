package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Goal;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.repositories.GoalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService extends BaseService<Goal, Long> {
    private GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Optional<Goal> findByUserAndSeason(User user, Integer year){
        return goalRepository.findByUserAndSeasonSeasonYear(user, year);
    }

    public List<Goal> findBySeason(Season season){
        return goalRepository.findBySeason(season);
    }

    @Override
    protected JpaRepository<Goal, Long> getRepository() {
        return goalRepository;
    }

    @Override
    public Class<Goal> getEntityClass() {
        return Goal.class;
    }

    @Override
    public Goal createEntity() {
        return new Goal();
    }


}
