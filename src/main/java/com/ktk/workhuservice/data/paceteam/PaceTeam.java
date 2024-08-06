package com.ktk.workhuservice.data.paceteam;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.users.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "PACE_TEAMS")
@FieldNameConstants
public class PaceTeam extends BaseEntity<PaceTeam, Long> {

    @Column(name="TEAM_LEADER")
    private User teamLeader;

    @Column(name = "COINS", columnDefinition = "float8 default 0")
    private double coins;

    @Size(max = 30)
    @Column(name = "TEAM_NAME", length = 30)
    @NotNull
    @NotEmpty
    private String teamName;

}
