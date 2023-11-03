package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.TransactionItem;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.repositories.TransactionItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TransactionItemService {

    private TransactionItemRepository transactionItemRepository;
    private RoundService roundService;

    public TransactionItemService(TransactionItemRepository transactionItemRepository, RoundService roundService) {
        this.transactionItemRepository = transactionItemRepository;
        this.roundService = roundService;
    }

    public Iterable<TransactionItem> findAllByUser(Long id) {
        return transactionItemRepository.findAllByUserId(id);
    }

    public Iterable<TransactionItem> findAllByUserIdAndRound(Long id, Round s) {
        return transactionItemRepository.findAllByUserIdAndRound(id, s);
    }

    public Iterable<TransactionItem> findAllByTransactionId(Long id) {
        return transactionItemRepository.findAllByTransactionId(id);
    }

    public Integer countByTransactionId(Long id) {
        return transactionItemRepository.countByTransactionId(id);
    }

    public void deleteByTransactionId(Long transactionId) {
        findAllByTransactionId(transactionId).forEach(t -> transactionItemRepository.deleteById(t.getId()));
    }

    public void deleteById(Long transactionItemId) {
        transactionItemRepository.deleteById(transactionItemId);
    }

    public void save(TransactionItem t) {
        if (t.getPoints() != 0.0 || t.getHours() != 0.0 || t.getCredit() != 0) {
            Optional<Round> transactionRound = roundService.findRoundByDate(t.getTransactionDate().atStartOfDay());
            t.setRound(transactionRound.isEmpty() ? roundService.findRoundByDate(LocalDateTime.now()).get() : transactionRound.get());
            if (t.getAccount().equals(Account.MYSHARE)) {
                if (t.getTransactionType().equals(TransactionType.CREDIT) && t.getCredit() != 0) {
                    double creditPoints = (double) t.getCredit() / 1000.0;
                    t.setPoints(creditPoints);
                } else if (t.getTransactionType().equals(TransactionType.HOURS) && t.getHours() != 0) {
                    t.setPoints(t.getHours() * 4.0);
                    t.setCredit((int) (t.getHours() * 2000));
                }

            } else if (t.getAccount().equals(Account.SAMVIRK)) {
                double creditPoints = (double) t.getCredit() / 1000.0;
                double samvirkpoints = StreamSupport.stream(transactionItemRepository.findAllByUserIdAndRoundAndAccount(t.getUser().getId(), t.getRound(), Account.SAMVIRK).spliterator(), false)
                        .mapToDouble(TransactionItem::getPoints).sum();
                double maxPoints = t.getRound().getSamvirkMaxPoints();
                double onTrackPoints = t.getRound().getSamvirkOnTrackPoints();
                if (maxPoints != 0 && creditPoints + samvirkpoints > maxPoints - onTrackPoints) {
                    t.setPoints(maxPoints - onTrackPoints - samvirkpoints < 0 ? 0 : maxPoints - onTrackPoints - samvirkpoints);
                } else {
                    t.setPoints(creditPoints);
                }

            } else if (t.getAccount().equals(Account.OTHER)) {
                t.setHours(0);
                t.setCredit(0);
            }

            t.setCreateDateTime(LocalDateTime.now());
            if (t.getTransactionDate() == null) {
                t.setTransactionDate(t.getCreateDateTime().toLocalDate());
            }
            transactionItemRepository.save(t);
        }
    }

    public void saveAll(Iterable<TransactionItem> transactions) {
        transactions.iterator().forEachRemaining(this::save);
    }

    public boolean existsById(Long transactionItemId) {
        return transactionItemRepository.existsById(transactionItemId);
    }
}
