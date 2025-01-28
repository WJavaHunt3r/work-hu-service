package com.ktk.workhuservice.data.userstatus;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.seasons.Season;
import com.ktk.workhuservice.data.users.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "USER_STATUS")
@FieldNameConstants
public class UserStatus extends BaseEntity<UserStatus, Long> {

    @JoinColumn(name = "USERS")
    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private Integer goal;

    @Column(name = "STATUS", columnDefinition = "float8 default 0")
    private double status;

    @Column(name = "TRANSACTIONS", columnDefinition = "integer default 0")
    private Integer transactions = 0;

    @Column(name = "TRANSITION", columnDefinition = "integer default 0")
    private Integer transition = 0;

    @JoinColumn(name = "SEASON")
    @ManyToOne
    private Season season;

}
