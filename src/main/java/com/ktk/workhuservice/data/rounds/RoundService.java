package com.ktk.workhuservice.data.rounds;

import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RoundService extends BaseService<Round, Long> {

    private RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public Iterable<Round> findAllByRoundYear(int year) {
        return roundRepository.findAllBySeasonSeasonYearAndActiveRound(year, true);
    }


    public Optional<Round> findRoundByDate(LocalDateTime dateTime) {
        return roundRepository.findRoundByDate(dateTime);
    }

    public Round getLastRound(){
        return findRoundByDate(LocalDateTime.now()).orElse(roundRepository.getLastRound(LocalDate.now().getYear()));
    }

    @Override
    protected JpaRepository<Round, Long> getRepository() {
        return roundRepository;
    }

    @Override
    public Class<Round> getEntityClass() {
        return Round.class;
    }

    @Override
    public Round createEntity() {
        return new Round();
    }
}
