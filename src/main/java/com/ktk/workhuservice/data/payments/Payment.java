package com.ktk.workhuservice.data.payments;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.donation.Donation;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.enums.PaymentGoal;
import com.ktk.workhuservice.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PAYMENTS")
@FieldNameConstants
public class Payment extends BaseEntity<Payment, Long> {

    @NotNull
    @Column(name = "AMOUNT", columnDefinition = "integer default 0")
    private Integer amount;

    @NotNull
    @NotEmpty
    @Column(name = "DESCRIPTION", length = 60)
    @Size(max = 60)
    private String description;

    @NotNull
    @NotEmpty
    @Column(name = "CHECKOUT_REFERENCE", length = 20)
    @Size(max = 20)
    private String checkoutReference;

    @NotNull
    @NotEmpty
    @Column(name = "CHECKOUT_ID", length = 40)
    @Size(max = 40)
    private String checkoutId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;

    @Column(name = "PAYMENT_GOAL")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentGoal paymentGoal;

    @JoinColumn(name = "USERS")
    @ManyToOne
    private User user;

    @JoinColumn(name = "DONATION")
    @ManyToOne
    private Donation donation;
}
