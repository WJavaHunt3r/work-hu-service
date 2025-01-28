package com.ktk.workhuservice.data.transactionitems;

import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.users.User;
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

    @Query("SELECT SUM(ti.credit) FROM TransactionItem ti where ti.round = ?2 and ti.user = ?1 ")
    Integer sumCreditByUserAndRound(User user, Round round);

    @Query("SELECT SUM(ti.points) FROM TransactionItem ti where ti.round = ?2 and ti.user = ?1 ")
    Double sumPointsByUserAndRound(User user, Round round);

    @Query("SELECT SUM(ti.credit) FROM TransactionItem ti where ti.round.season.seasonYear = ?2 and ti.user = ?1 and ti.account = ?3")
    Integer sumCreditByUserAndSeasonYear(User user, Integer seasonYear, Account account);

    Iterable<TransactionItem> findAllByTransactionId(Long id);

    Iterable<TransactionItem> findAllByUserAndRoundAndAccount(User u, Round round, Account account);

    Integer countByTransactionId(Long id);

    void deleteAllByTransactionId(Long id);
}
