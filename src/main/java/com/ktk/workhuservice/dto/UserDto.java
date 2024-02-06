package com.ktk.workhuservice.dto;

import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String firstname;

    private String lastname;

    private LocalDate birthDate;

    private Team team;

    private Role role;

    private long myShareID;

    private Integer baseMyShareCredit;

    private Integer currentMyShareCredit;

    private boolean changedPassword;

    private Long spouseId;

    private Long familyId;

}
