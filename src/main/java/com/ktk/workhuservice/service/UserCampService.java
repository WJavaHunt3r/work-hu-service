package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.data.UserCamp;
import com.ktk.workhuservice.repositories.UserCampRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCampService extends BaseService<UserCamp, Long> {

    private UserCampRepository userCampRepository;

    public UserCampService(UserCampRepository userCampRepository) {
        this.userCampRepository = userCampRepository;
    }

    public List<UserCamp> fetchByQuery(User user, Camp camp, Season season, Boolean participates){
        return userCampRepository.fetchByQuery(user, camp, season, participates);
    }

    @Override
    protected JpaRepository<UserCamp, Long> getRepository() {
        return userCampRepository;
    }

    @Override
    public Class<UserCamp> getEntityClass() {
        return UserCamp.class;
    }

    @Override
    public UserCamp createEntity() {
        return new UserCamp();
    }

    public List<UserCamp> findAllByUser(User user){
        return userCampRepository.findAllByUser(user);
    }
}
