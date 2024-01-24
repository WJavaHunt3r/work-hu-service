package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.UserCamp;
import com.ktk.workhuservice.dto.UserCampDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserCampMapper extends BaseMapper<UserCamp, UserCampDto> {
    protected UserCampMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    public UserCampDto entityToDto(UserCamp entity) {
        return modelMapper.map(entity, UserCampDto.class);
    }

    @Override
    public UserCamp dtoToEntity(UserCampDto dto, UserCamp entity) {
        UserCamp userCamp = modelMapper.map(dto, UserCamp.class);
        userCamp.setUser(entity.getUser());
        return userCamp;
    }
}
