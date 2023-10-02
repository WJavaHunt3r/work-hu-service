package com.ktk.workhuservice.data;

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

    @Column(name = "GOAL")
    @NotNull
    private Integer goal = 0;

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

    public String getFullName() {
        return lastname + " " + firstname;
    }

    public void addPoints(double points) {
        this.points += points;
    }
}
