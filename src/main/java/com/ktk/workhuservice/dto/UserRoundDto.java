package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserRoundDto {

    private UserDto user;

    private Round round;

    private boolean myShareOnTrackPoints;

    private boolean samvirkOnTrackPoints;

    private double samvirkPoints;

    private Integer samvirkPayments;

    private double roundPoints;

    private double forbildePoints;
}
