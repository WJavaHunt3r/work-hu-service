package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.seasons.Season;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GoalDto {
    private Long id;

    private UserDto user;

    private Season season;

    private Integer goal;
}
