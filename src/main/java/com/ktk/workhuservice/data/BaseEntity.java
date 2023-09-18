package com.ktk.workhuservice.data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity<T, I extends Serializable> implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected I id;

    protected I getId() {
        return id;
    }

    protected void setId(I id) {
        this.id = id;
    }
}

