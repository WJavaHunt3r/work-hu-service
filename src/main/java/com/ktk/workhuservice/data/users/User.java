package com.ktk.workhuservice.data.users;

import com.ktk.workhuservice.data.BaseEntity;
import com.ktk.workhuservice.data.paceteam.PaceTeam;
import com.ktk.workhuservice.data.teams.Team;
import com.ktk.workhuservice.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Entity
@Table(name = "USERS")
@FieldNameConstants
public class User extends BaseEntity<User, Long> {

    @Size(max = 50)
    @Column(name = "FIRSTNAME", length = 50)
    @NotNull
    @NotEmpty
    private String firstname;

    @Size(max = 50)
    @Column(name = "LASTNAME", length = 50)
    @NotNull
    @NotEmpty
    private String lastname;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "TEAMS")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "PACE_TEAMS")
    private PaceTeam paceTeam;

    @Size(max = 30)
    @Column(name = "USERNAME", length = 30)
    @NotNull
    @NotEmpty
    private String username;

    @Size(max = 1000)
    @Column(name = "PASSWORD", length = 1000)
    @NotNull
    @NotEmpty
    private String password;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Column(name = "MYSHARE_ID")
    @NotNull
    private long myShareID;

    @Column(name = "BASE_MYSHARE_CREDIT")
    private Integer baseMyShareCredit;

    @Column(name = "CURRENT_MYSHARE_CREDIT")
    private Integer currentMyShareCredit;

    @Column(name = "SAMVIRK_PAYMENTS")
    private Integer samvirkPayments = 0;

    @Column(name = "POINTS", columnDefinition = "float8 default 0")
    private double points;

    @Column(name = "CHANGED_PASSWORD")
    private boolean changedPassword;

    @JoinColumn(name = "SPOUSE_ID")
    private Long spouseId;

    @Column(name = "FAMILY_ID")
    private Long familyId;

    @Column(name = "PHONE_NUMBER")
    private Long phoneNumber;

    @Size(max = 200)
    @Column(name = "EMAIL", length = 200)
    private String email;

    public String getFullName() {
        return lastname + " " + firstname;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public int getAge() {
        return getAgeAtDate(LocalDate.now());
    }

    public int getAgeAtDate(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }
}
