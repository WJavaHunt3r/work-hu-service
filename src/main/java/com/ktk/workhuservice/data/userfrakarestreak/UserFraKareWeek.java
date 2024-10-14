package com.ktk.workhuservice.data.userfrakarestreak;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.frakareweek.FraKareWeek;
import com.ktk.workhuservice.data.users.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "USER_FRA_KARE_WEEK", uniqueConstraints =
        {@UniqueConstraint(name = "UniqueWeekAndUser", columnNames = {"FRA_KARE_WEEK", "USERS"})})
@FieldNameConstants
public class UserFraKareWeek extends BaseEntity<UserFraKareWeek, Long> {

    @NotNull
    @JoinColumn(name = "USERS")
    @ManyToOne
    private User user;

    @NotNull
    @JoinColumn(name = "FRA_KARE_WEEK")
    @ManyToOne
    private FraKareWeek fraKareWeek;

    @Column(name = "LISTENED", columnDefinition = "boolean default false")
    @NotNull
    private boolean listened;
}
