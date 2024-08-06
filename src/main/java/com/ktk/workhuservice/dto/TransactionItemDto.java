package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class TransactionItemDto {

    private long id;

    private Long transactionId;

    private LocalDate transactionDate;

    private String description;

    private UserDto user;

    private Long createUserId;

    private double points;

    private TransactionType transactionType;

    private Account account;

    private double hours;

    private Integer credit;

    private Round round;
}
