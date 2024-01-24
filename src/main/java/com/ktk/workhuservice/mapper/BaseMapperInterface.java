package com.ktk.workhuservice.mapper;

public interface BaseMapperInterface<C, D> {
    D entityToDto(C entity);

    C dtoToEntity(D dto, C entity);
}
