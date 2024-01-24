package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.repositories.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService extends BaseService<Transaction, Long> {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction t) {
        if (t.getCreateDateTime() == null) {
            t.setCreateDateTime(LocalDateTime.now());
        }
        return transactionRepository.save(t);
    }

    public Iterable<Transaction> findAllByCreateUser(User createUser) {
        return transactionRepository.findAllByCreateUser(createUser);
    }

    public Optional<Transaction> findByName(String name) {
        return transactionRepository.findByName(name);
    }

    @Override
    protected JpaRepository<Transaction, Long> getRepository() {
        return transactionRepository;
    }

    @Override
    public Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    public Transaction createEntity() {
        return new Transaction();
    }
}
