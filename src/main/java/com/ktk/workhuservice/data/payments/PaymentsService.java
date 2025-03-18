package com.ktk.workhuservice.data.payments;

import com.ktk.workhuservice.enums.PaymentStatus;
import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentsService extends BaseService<Payment, Long> {
    private final PaymentRepository repository;

    public PaymentsService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> fetchByQuery(PaymentStatus status, Long userId, Long donationId) {
        return repository.fetchByQuery(status, userId, donationId);
    }

    @Override
    protected JpaRepository<Payment, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<Payment> getEntityClass() {
        return Payment.class;
    }

    @Override
    public Payment createEntity() {
        return new Payment();
    }
}
