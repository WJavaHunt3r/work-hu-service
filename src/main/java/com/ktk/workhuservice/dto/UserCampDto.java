package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.Camp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserCampDto {

    private long id;

    private UserDto user;

    private Camp camp;

    private boolean participates;

    private Integer price;
}
