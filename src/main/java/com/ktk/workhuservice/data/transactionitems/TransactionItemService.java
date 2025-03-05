package com.ktk.workhuservice.data.transactionitems;

import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionItemService {

    private final TransactionItemRepository transactionItemRepository;

    public TransactionItemService(TransactionItemRepository transactionItemRepository) {
        this.transactionItemRepository = transactionItemRepository;
    }

    public Optional<TransactionItem> findById(Long id) {
        return transactionItemRepository.findById(id);
    }

    public List<TransactionItem> fetchByQuery(TransactionType transactionType, LocalDate startDate, LocalDate endDate, Long transactionId, Long roundId, Long userId, Integer seasonYear) {
        return transactionItemRepository.fetchByQuery(transactionType, startDate, endDate, transactionId, roundId, userId, seasonYear);
    }

    public Iterable<TransactionItem> findAllByUser(User user) {
        return transactionItemRepository.findAllByUser(user);
    }

    public List<TransactionItem> findAllByUserAndSeasonYear(User user, Integer seasonYear) {
        return transactionItemRepository.findAllByUserAndSeasonYear(user, seasonYear);
    }

    public Iterable<TransactionItem> findAllByUserAndRound(User user, Round s) {
        return transactionItemRepository.findAllByUserAndRound(user, s);
    }

    public Integer sumCreditByUserAndRound(User user, Round s) {
        return transactionItemRepository.sumCreditByUserAndRound(user, s);
    }

    public Double sumPointsByUserAndRound(User user, Round s) {
        return transactionItemRepository.sumPointsByUserAndRound(user, s);
    }

    public Integer sumCreditByUserAndSeasonYear(User user, Integer year, Account account) {
        return transactionItemRepository.sumCreditByUserAndSeasonYear(user, year, account);
    }

    public Iterable<TransactionItem> findAllByTransactionId(Long id) {
        return transactionItemRepository.findAllByTransactionId(id);
    }

    public Integer countByTransactionId(Long id) {
        return transactionItemRepository.countByTransactionId(id);
    }

    public void deleteByTransactionId(Long transactionId) {
        fetchByQuery(null, null, null, transactionId, null, null, null).forEach(t -> transactionItemRepository.deleteById(t.getId()));
    }

    public void deleteById(Long transactionItemId) {
        transactionItemRepository.deleteById(transactionItemId);
    }

    public TransactionItem save(TransactionItem t) {
        if (t.getPoints() != 0.0 || t.getHours() != 0.0 || t.getCredit() != 0) {
            Round transactionRound = t.getRound();
            if (t.getAccount().equals(Account.MYSHARE)) {
                if (t.getTransactionType().equals(TransactionType.CREDIT) && t.getCredit() != 0) {
                    double creditPoints = (double) t.getCredit() / 1000.0;
                    t.setPoints(creditPoints);
                } else if (Arrays.asList(TransactionType.DUKA_MUNKA_2000, TransactionType.HOURS).contains(t.getTransactionType()) && t.getHours() != 0) {
                    t.setPoints(t.getHours() * 6.0);
                    t.setCredit((int) (t.getHours() * 2000));
                } else if (t.getTransactionType().equals(TransactionType.DUKA_MUNKA) && t.getHours() != 0) {
                    t.setPoints(t.getHours() * 6.0);
                    t.setCredit((int) (t.getHours() * 1000));
                }

            } else if (t.getAccount().equals(Account.SAMVIRK)) {
//                double creditPoints = (double) t.getCredit() / 1000.0;
//                double samvirkpoints = StreamSupport.stream(transactionItemRepository.findAllByUserAndRoundAndAccount(t.getUser(), t.getRound(), Account.SAMVIRK).spliterator(), false)
//                        .mapToDouble(TransactionItem::getPoints).sum();
//                double maxPoints = t.getRound().getSamvirkMaxPoints();
//                double onTrackPoints = t.getRound().getSamvirkOnTrackPoints();
//                if (maxPoints != 0 && creditPoints + samvirkpoints > maxPoints - onTrackPoints) {
//                    t.setPoints(maxPoints - onTrackPoints - samvirkpoints < 0 ? 0 : maxPoints - onTrackPoints - samvirkpoints);
//                } else {
//                    t.setPoints(creditPoints);
//                }

            } else if (t.getAccount().equals(Account.OTHER)) {
                t.setHours(0);
                t.setCredit(0);
            }

            t.setCreateDateTime(LocalDateTime.now());
            if (t.getTransactionDate() == null) {
                t.setTransactionDate(t.getCreateDateTime().toLocalDate());
            }
            return transactionItemRepository.save(t);
        }
        return t;
    }

    public void saveAll(Iterable<TransactionItem> transactions) {
        transactions.iterator().forEachRemaining(this::save);
    }

}
