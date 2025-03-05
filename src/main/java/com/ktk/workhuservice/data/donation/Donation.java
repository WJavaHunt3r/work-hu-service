package com.ktk.workhuservice.data.donation;

import com.ktk.workhuservice.data.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "DONATIONS")
@FieldNameConstants
public class Donation extends BaseEntity<Donation, Long> {


}
