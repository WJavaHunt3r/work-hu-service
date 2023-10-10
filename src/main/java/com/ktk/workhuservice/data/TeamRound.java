package com.ktk.workhuservice.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "TEAM_ROUNDS")
@FieldNameConstants
public class TeamRound extends BaseEntity<TeamRound, Long> {
    @JoinColumn(name = "TEAM")
    @ManyToOne
    @NotNull
    private Team team;

    @JoinColumn(name = "ROUND")
    @ManyToOne
    @NotNull
    private Round round;

    @Column(name = "TEAM_POINTS", columnDefinition = "float8 default 0")
    private double teamPoints;

}
