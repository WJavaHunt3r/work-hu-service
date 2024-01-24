package com.ktk.workhuservice.mapper;

import org.modelmapper.ModelMapper;

abstract class BaseMapper<C, D> implements BaseMapperInterface<C, D>{

    ModelMapper modelMapper;

    protected BaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
