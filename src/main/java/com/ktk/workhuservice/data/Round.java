package com.ktk.workhuservice.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ROUNDS")
@FieldNameConstants
public class Round extends BaseEntity<Round, Long> {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "START_DATE")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "END_DATE")
    private LocalDateTime endDateTime;

    @Column(name = "MYSHARE_GOAL")
    @NotNull
    private Integer myShareGoal;

    @Column(name = "SAMVIRK_GOAL")
    @NotNull
    private Integer samvirkGoal;

    @Column(name = "ROUND_NUMBER")
    @NotNull
    private Integer roundNumber;
}
