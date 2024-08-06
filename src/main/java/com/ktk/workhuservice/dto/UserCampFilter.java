package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.camps.Camp;
import com.ktk.workhuservice.data.seasons.Season;
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
