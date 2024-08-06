package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.mentormentee.MentorMentee;
import com.ktk.workhuservice.data.users.User;
import com.ktk.workhuservice.dto.MentorMenteeDto;
import com.ktk.workhuservice.dto.UserDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;

@Service
public class MentorMenteeMapper extends BaseMapper<MentorMentee, MentorMenteeDto> {

    protected MentorMenteeMapper(ModelMapper modelMapper, UserMapper userMapper) {
        super(modelMapper);
        TypeMap<MentorMentee, MentorMenteeDto> propertyMapper = modelMapper.createTypeMap(MentorMentee.class, MentorMenteeDto.class);
        Converter<User, UserDto> userToDto = c -> userMapper.entityToDto(c.getSource());
        propertyMapper.addMappings(
                mapper -> mapper.using(userToDto).map(MentorMentee::getMentor, MentorMenteeDto::setMentor)
        );
        propertyMapper.addMappings(
                mapper -> mapper.using(userToDto).map(MentorMentee::getMentee, MentorMenteeDto::setMentee)
        );
    }

    @Override
    public MentorMenteeDto entityToDto(MentorMentee entity) {
        return modelMapper.map(entity, MentorMenteeDto.class);
    }

    @Override
    public MentorMentee dtoToEntity(MentorMenteeDto dto, MentorMentee entity) {
        return entity;
    }
}
