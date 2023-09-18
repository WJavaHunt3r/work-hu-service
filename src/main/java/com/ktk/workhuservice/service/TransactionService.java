package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Iterable<Transaction> getAllByUser(User u) {
        return transactionRepository.findAllByUser(u);
    }

    public Iterable<Transaction> getAllByUserAndSeason(User u, Season s) {
        return transactionRepository.findAllByUserAndSeason(u, s);
    }

    public void save(Transaction t) {
        if(t.getAccount().equals(Account.MYSHARE)){
            if(t.getTransactionType().equals(TransactionType.CASH)){
                t.setPoints((double)t.getValue()/1000);
            } else if(t.getTransactionType().equals(TransactionType.HOURS)){
                t.setPoints(t.getValue() * 4);
            }
        } else if(t.getAccount().equals(Account.SAMVIRK)){
            t.setPoints((double)t.getValue()/1000);
        }
        transactionRepository.save(t);
    }

    public void saveAll(Iterable<Transaction> transactions) {
        transactions.iterator().forEachRemaining(this::save);
    }

}
