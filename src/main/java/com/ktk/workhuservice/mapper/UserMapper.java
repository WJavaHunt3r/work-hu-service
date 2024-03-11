package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserMapper extends BaseMapper<User, UserDto> {

    public UserMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    public User dtoToEntity(UserDto dto, User user) {
        user.setRole(dto.getRole());
        user.setTeam(dto.getTeam());
        user.setBirthDate(dto.getBirthDate());
        user.setLastname(dto.getLastname());
        user.setFirstname(dto.getFirstname());
        user.setBaseMyShareCredit(dto.getBaseMyShareCredit());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }

    public UserDto entityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
