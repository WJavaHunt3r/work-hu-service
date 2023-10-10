package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.repositories.RoundRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RoundService {

    private RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public Round save(Round s) {
        return roundRepository.save(s);
    }

    public Iterable<Round> getAll() {
        return roundRepository.findAll();
    }

    public long countAll() {
        return roundRepository.count();
    }

    public Optional<Round> findById(Long id) {
        return roundRepository.findById(id);
    }

    public Optional<Round> findByRoundNumber(Integer roundNumber) {
        return roundRepository.findByRoundNumber(roundNumber);
    }

    public Optional<Round> findRoundByDate(LocalDateTime dateTime) {
        return roundRepository.findRoundByDate(dateTime);
    }
}
