package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.repositories.SeasonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SeasonService {

    private SeasonRepository seasonRepository;

    public SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public void save(Season s) {
        seasonRepository.save(s);
    }

    public Iterable<Season> getAll() {
        return seasonRepository.findAll();
    }

    public long countAll() {
        return seasonRepository.count();
    }

    public Optional<Season> findBySeasonYear(Integer year) {
        return seasonRepository.findBySeasonYear(year);
    }

    public Optional<Season> findCurrentSeason() {
        return seasonRepository.findCurrentSeason(LocalDate.now());
    }
}
