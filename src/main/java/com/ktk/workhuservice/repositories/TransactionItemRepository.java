package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.enums.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {

    Iterable<TransactionItem> findAllByUserId(Long id);

    Iterable<TransactionItem> findAllByUserIdAndRound(Long id, Round s);

    Iterable<TransactionItem> findAllByTransactionId(Long id);

    Iterable<TransactionItem> findAllByUserIdAndRoundAndAccount(Long id, Round s, Account account);

    Integer countByTransactionId(Long id);

    void deleteAllByTransactionId(Long id);
}
