package com.ktk.workhuservice.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ACTIVITY")
@FieldNameConstants
public class Activity extends BaseEntity<Activity, Long> {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "CREATE_DATE_TIME")
    private LocalDateTime createDateTime;

    @JoinColumn(name = "CREATE_USER")
    @ManyToOne
    @NotNull
    private User createUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "ACTIVITY_DATE_TIME")
    private LocalDateTime activityDateTime;

    @Size(max = 200)
    @Column(name = "DESCRIPTION", length = 200)
    @NotNull
    @NotEmpty
    private String description;

    @JoinColumn(name = "EMPLOYER")
    @ManyToOne
    @NotNull
    private User employer;

    @JoinColumn(name = "RESPONSIBLE")
    @ManyToOne
    @NotNull
    private User responsible;

    @Column(name = "ACTIVITY_ID")
    private Integer activityId;

    @Column(name = "REGISTERED_IN_APP")
    private boolean registeredInApp;

    @Column(name = "REGISTERED_IN_MYSHARE")
    private boolean registeredInMyShare;

    @Column(name = "PAID")
    private boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @Column(name = "PAID_AMOUNT", columnDefinition = "integer default 0")
    private Integer paidAmount = 0;
}
