package com.ktk.workhuservice.mapper;

import com.ktk.workhuservice.data.donation.Donation;
import com.ktk.workhuservice.data.payments.PaymentsService;
import com.ktk.workhuservice.dto.DonationDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DonationMapper extends BaseMapper<Donation, DonationDto> {

    private final PaymentsService paymentsService;
    public DonationMapper(ModelMapper modelMapper, PaymentsService paymentsService) {
        super(modelMapper);
        this.paymentsService = paymentsService;
    }

    @Override
    public DonationDto entityToDto(Donation entity) {
        DonationDto dto =  modelMapper.map(entity, DonationDto.class);
        dto.setSum(paymentsService.sumByDonation(entity.getId()));
        return dto;
    }

    @Override
    public Donation dtoToEntity(DonationDto dto, Donation entity) {
        return modelMapper.map(dto, Donation.class);
    }
}
