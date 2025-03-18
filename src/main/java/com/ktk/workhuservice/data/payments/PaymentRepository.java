package com.ktk.workhuservice.data.payments;

import com.ktk.workhuservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p where (p.status = ?1 or ?1 is null) " +
            " AND (p.user.id = ?2 or ?2 is null) " +
            " AND (p.donation.id = ?3 or ?3 is null) ")
    List<Payment> fetchByQuery(PaymentStatus status, Long userId, Long donationId);
}
