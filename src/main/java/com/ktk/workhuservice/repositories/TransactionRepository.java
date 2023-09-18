package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Iterable<Transaction> findAllByUser(User u);

    Iterable<Transaction> findAllByUserAndSeason(User u, Season s);
}
