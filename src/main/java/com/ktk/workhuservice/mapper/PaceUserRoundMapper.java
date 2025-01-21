package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.paceuserround.PaceUserRound;
import com.ktk.workhuservice.dto.PaceUserRoundDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PaceUserRoundMapper extends BaseMapper<PaceUserRound, PaceUserRoundDto> {
    private UserMapper userMapper;

    protected PaceUserRoundMapper(ModelMapper modelMapper, UserMapper userMapper) {
        super(modelMapper);
        this.userMapper = userMapper;
    }

    @Override
    public PaceUserRoundDto entityToDto(PaceUserRound entity) {
        PaceUserRoundDto dto = modelMapper.map(entity, PaceUserRoundDto.class);
        dto.setUser(userMapper.entityToDto(entity.getUser()));
        return dto;

    }

    @Override
    public PaceUserRound dtoToEntity(PaceUserRoundDto dto, PaceUserRound entity) {
        return null;
    }
}
