package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.frakareweek.FraKareWeek;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class UserFraKareWeekDto {

    private Long id;

    private UserDto user;

    private FraKareWeek fraKareWeek;

    private boolean listened;
}
