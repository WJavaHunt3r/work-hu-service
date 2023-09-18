package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.repositories.SeasonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Optional<Season> findBySeasonNumber(Integer seasonNumber) {
        return seasonRepository.findBySeasonNumber(seasonNumber);
    }

    public Optional<Season> findCurrentSeason() {
        return seasonRepository.findCurrentSeason(LocalDateTime.now());
    }
}
