package com.ktk.workhuservice.data.paceuserround;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.rounds.Round;
import com.ktk.workhuservice.data.users.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "PACE_USER_ROUNDS", uniqueConstraints =
        {@UniqueConstraint(name = "UniqueRoundAndUser", columnNames = {"ROUNDS", "USERS"})})
@FieldNameConstants
public class PaceUserRound extends BaseEntity<PaceUserRound, Long> {

    @JoinColumn(name = "USERS")
    @ManyToOne
    @NotNull
    private User user;

    @JoinColumn(name = "ROUNDS")
    @ManyToOne
    @NotNull
    private Round round;

    @Column(name = "SAMVIRK_PAYMENTS", columnDefinition = "integer default 0")
    private Integer samvirkPayments = 0;

    @Column(name = "ROUND_COINS", columnDefinition = "integer default 0")
    private Integer roundCoins;

    @Column(name = "ROUND_CREDITS", columnDefinition = "integer default 0")
    private Integer roundCredits;

    @Column(name = "BMM_PERFECT_WEEK_POINTS", columnDefinition = "float8 default 0")
    private double bMMPerfectWeekPoints;

    @Column(name = "ROUND_MYSHARE_GOAL", columnDefinition = "integer default 0")
    private Integer roundMyShareGoal;

    public void addSamvirkPayment(Integer payment) {
        samvirkPayments += payment;
    }

    public void addRoundPoints(int coins) {
        roundCoins += coins;
    }

    public void addBMMPerfectWeekPoints(double points) {
        bMMPerfectWeekPoints += points;
    }
}
