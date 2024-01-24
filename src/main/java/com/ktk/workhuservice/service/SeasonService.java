package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.repositories.SeasonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SeasonService extends BaseService<Season, Long>{

    private SeasonRepository seasonRepository;

    public SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public Optional<Season> findBySeasonYear(Integer year) {
        return seasonRepository.findBySeasonYear(year);
    }

    public Optional<Season> findCurrentSeason() {
        return seasonRepository.findCurrentSeason(LocalDate.now());
    }

    @Override
    protected JpaRepository<Season, Long> getRepository() {
        return seasonRepository;
    }

    @Override
    public Class<Season> getEntityClass() {
        return Season.class;
    }

    @Override
    public Season createEntity() {
        return new Season();
    }
}
