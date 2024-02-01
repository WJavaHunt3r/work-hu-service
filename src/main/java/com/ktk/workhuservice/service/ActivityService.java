package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Activity;
import com.ktk.workhuservice.repositories.ActivityRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService extends BaseService<Activity, Long>{

    private ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public List<Activity> fetchByQuery(Long responsible, Long employer, Boolean registeredInApp, Boolean registeredInMyShare, Long createUser) {
        return repository.fetchByQuery(responsible, employer, registeredInApp, registeredInMyShare, createUser);
    }

    public Optional<Activity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected JpaRepository<Activity, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<Activity> getEntityClass() {
        return Activity.class;
    }

    @Override
    public Activity createEntity() {
        return new Activity();
    }
}
