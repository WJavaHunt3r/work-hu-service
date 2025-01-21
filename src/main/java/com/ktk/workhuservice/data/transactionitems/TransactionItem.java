package com.ktk.workhuservice.data.transactionitems;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.data.rounds.Round;
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
@Table(name = "TRANSACTION_ITEMS")
@FieldNameConstants
public class TransactionItem extends BaseEntity<TransactionItem, Long> {

    @NotNull
    private Long transactionId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Size(max = 150)
    @Column(name = "DESCRIPTION", length = 150)
    @NotNull
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "USERS")
    private User user;

    @Column(name = "POINTS", columnDefinition = "float8 default 0")
    @NotNull
    private double points;

    @ManyToOne
    @JoinColumn(name = "CREATE_USER")
    private User createUser;

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

    @Column(name = "HOURS", columnDefinition = "float8 default 0")
    private double hours;

    @Column(name = "CREDIT", columnDefinition = "integer default 0")
    private Integer credit;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ROUNDS")
    private Round round;

}
