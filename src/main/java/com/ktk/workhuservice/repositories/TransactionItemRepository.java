package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {

    Iterable<TransactionItem> findAllByUser(User user);

    @Query("SELECT ti FROM TransactionItem ti where ti.round.season.seasonYear = ?2 and ti.user = ?1 ")
    List<TransactionItem> findAllByUserAndSeasonYear(User user, Integer seasonYear);

    Iterable<TransactionItem> findAllByUserAndRound(User user, Round s);

    Iterable<TransactionItem> findAllByTransactionId(Long id);

    Iterable<TransactionItem> findAllByUserAndRoundAndAccount(User u, Round round, Account account);

    Integer countByTransactionId(Long id);

    void deleteAllByTransactionId(Long id);
}
