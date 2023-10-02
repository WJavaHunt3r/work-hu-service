package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.enums.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class TransactionDto {
    private Long id;

    private String name;

    private Account account;

    private LocalDateTime createDateTime;

    private Integer transactionCount;
}
