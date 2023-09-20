package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Iterable<Transaction> findAllByUserId(Long id);

    Iterable<Transaction> findAllByUserIdAndSeason(Long id, Season s);
}
