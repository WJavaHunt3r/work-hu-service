package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Iterable<Transaction> findAllByCreateUser(User createUser);

    Optional<Transaction> findByName(String name);
}
