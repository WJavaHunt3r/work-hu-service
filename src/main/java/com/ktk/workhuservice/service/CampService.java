package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.repositories.CampRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampService extends BaseService<Camp, Long>{

    private CampRepository campRepository;

    public CampService(CampRepository campRepository) {
        this.campRepository = campRepository;
    }

    @Override
    protected JpaRepository<Camp, Long> getRepository() {
        return campRepository;
    }

    public List<Camp> findAllBySeason(Season season){
        return  campRepository.findAllBySeason(season);
    }

    @Override
    public Class<Camp> getEntityClass() {
        return Camp.class;
    }

    @Override
    public Camp createEntity() {
        return new Camp();
    }
}
