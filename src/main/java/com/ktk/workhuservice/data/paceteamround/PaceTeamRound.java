package com.ktk.workhuservice.data.paceteamround;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.paceteam.PaceTeam;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "PACE_TEAM_ROUNDS")
@FieldNameConstants
public class PaceTeamRound extends BaseEntity<PaceTeamRound, Long> {
    @JoinColumn(name = "TEAM")
    @ManyToOne
    @NotNull
    private PaceTeam team;

    @JoinColumn(name = "ROUND")
    @ManyToOne
    @NotNull
    private Round round;

    @Column(name = "TEAM_ROUND_COINS", columnDefinition = "integer default 0")
    private Integer teamRoundCoins;

    @Column(name = "MAX_TEAM_ROUND_COINS", columnDefinition = "integer default 0")
    private Integer maxTeamRoundCoins;

    @Column(name = "TEAM_ROUND_STATUS", columnDefinition = "float default 0")
    private double teamRoundStatus;
}
