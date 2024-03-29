package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.*;
import com.ktk.workhuservice.repositories.ActivityRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService extends BaseService<Activity, Long> {

    private ActivityRepository repository;
    private TransactionItemService transactionItemService;
    private TransactionService transactionService;
    private ActivityItemService activityItemService;

    public ActivityService(ActivityRepository repository, TransactionItemService transactionItemService, TransactionService transactionService, ActivityItemService activityItemService) {
        this.repository = repository;
        this.transactionItemService = transactionItemService;
        this.transactionService = transactionService;
        this.activityItemService = activityItemService;
    }

    public List<Activity> fetchByQuery(Long responsible, Long employer, Boolean registeredInApp, Boolean registeredInMyShare, Long createUser) {
        return repository.fetchByQuery(responsible, employer, registeredInApp, registeredInMyShare, createUser);
    }

    public Optional<Activity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected JpaRepository<Activity, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<Activity> getEntityClass() {
        return Activity.class;
    }

    @Override
    public Activity createEntity() {
        return new Activity();
    }

    public void registerActivity(Activity activity, User createUser) {
        Transaction transaction = createTransaction(activity, createUser);

        for (var item : activityItemService.findByActivity(activity.getId())) {
            createTransactionItem(transaction, createUser, item);
        }
        activity.setRegisteredInApp(true);
    }

    private void createTransactionItem(Transaction transaction, User createUser, ActivityItem item) {
        TransactionItem transactionItem = new TransactionItem();
        transactionItem.setHours(item.getHours());
        transactionItem.setTransactionDate(item.getActivity().getActivityDateTime().toLocalDate());
        transactionItem.setRound(item.getRound());
        transactionItem.setTransactionId(transaction.getId());
        transactionItem.setCreateDateTime(LocalDateTime.now());
        transactionItem.setCreateUser(createUser);
        transactionItem.setUser(item.getUser());
        transactionItem.setDescription(item.getDescription());
        transactionItem.setAccount(item.getAccount());
        transactionItem.setTransactionType(item.getTransactionType());

        transactionItemService.save(transactionItem);
    }

    private Transaction createTransaction(Activity activity, User createUser) {
        Transaction transaction = new Transaction();
        transaction.setCreateUser(createUser);
        transaction.setAccount(activity.getAccount());
        transaction.setTransactionType(activity.getTransactionType());
        transaction.setName(activity.getDescription());

        return transactionService.save(transaction);
    }
}
