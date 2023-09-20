package com.ktk.workhuservice.data;

import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TRANSACTIONS")
@FieldNameConstants
public class Transaction extends BaseEntity<Transaction, Long> {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Size(max = 150)
    @Column(name = "DESCRIPTION", length = 150)
    @NotNull
    private String description;

    @NotNull
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "POINTS")
    @NotNull
    private double points;

    @NotNull
    @Column(name = "CREATE_USER_ID")
    private Long createUserId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "CREATE_DATE_TIME")
    private LocalDateTime createDateTime;

    @Column(name = "TRANSACTION_TYPE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionType transactionType;

    @Column(name = "ACCOUNT")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Account account;

    @NotNull
    @Column(name = "VALUE")
    private Integer value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "SEASONS")
    private Season season;

}
