package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.userstatus.UserStatus;
import com.ktk.workhuservice.dto.UserStatusDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserStatusMapper  extends BaseMapper<UserStatus, UserStatusDto> {
    public UserStatusMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    public UserStatusDto entityToDto(UserStatus entity) {
        return modelMapper.map(entity, UserStatusDto.class);
    }

    @Override
    public UserStatus dtoToEntity(UserStatusDto dto, UserStatus entity) {
        UserStatus userStatus = modelMapper.map(dto, UserStatus.class);
        userStatus.setUser(entity.getUser());
        return userStatus;
    }
}
