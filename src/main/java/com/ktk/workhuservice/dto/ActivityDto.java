package com.ktk.workhuservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ActivityDto {

    private LocalDateTime createDateTime;

    private UserDto createUser;

    private LocalDateTime activityDateTime;

    private String description;

    private UserDto employer;

    private UserDto responsible;

    private Integer activityId;

    private boolean registeredInApp;

    private boolean registeredInMyShare;

    private boolean paid;

    private LocalDate paymentDate;

    private Integer paidAmount = 0;
}
