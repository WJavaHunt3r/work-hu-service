package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.enums.Account;
import com.ktk.workhuservice.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ActivityDto {
    private Long id;

    private LocalDateTime createDateTime;

    private UserDto createUser;

    private LocalDateTime activityDateTime;

    private String description;

    private UserDto employer;

    private UserDto responsible;

    private Integer activityId;

    private boolean registeredInApp;

    private boolean registeredInMyShare;

    private boolean registeredInTeams;

    private TransactionType transactionType;

    private Account account;
}
