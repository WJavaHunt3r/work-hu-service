package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction save(Transaction t){
        if(t.getCreateDateTime() == null){
            t.setCreateDateTime(LocalDateTime.now());
        }
        return transactionRepository.save(t);
    }

    public Optional<Transaction> findById(Long id){
        return transactionRepository.findById(id);
    }

    public Iterable<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Iterable<Transaction> findAllByCreateUser(User createUser){
        return transactionRepository.findAllByCreateUser(createUser);
    }

    public Optional<Transaction> findByName(String name){
        return transactionRepository.findByName(name);
    }

    public boolean existsById(Long id){
        return transactionRepository.existsById(id);
    }

    public void deleteById(Long id){
        transactionRepository.deleteById(id);
    }
}
