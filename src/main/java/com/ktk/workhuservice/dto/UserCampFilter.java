package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.Camp;
import com.ktk.workhuservice.data.Season;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserCampFilter {

    private UserDto user;

    private Camp camp;

    private boolean participates;

    private Season season;

}
