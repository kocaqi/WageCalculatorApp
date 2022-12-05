package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.repository.OffDayRepository;
import com.localweb.wagecalculatorapp.repository.UserRepository;
import com.localweb.wagecalculatorapp.repository.WorkingDayRepository;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class Service implements ServiceInterface{
    UserRepository userRepository;
    WorkingDayRepository workingDayRepository;
    OffDayRepository offDayRepository;

    @Autowired
    public Service(UserRepository userRepository, WorkingDayRepository workingDayRepository, OffDayRepository offDayRepository) {
        this.userRepository = userRepository;
        this.workingDayRepository = workingDayRepository;
        this.offDayRepository = offDayRepository;
    }

    @Override
    public ResponseDTO calculateWage(RequestDTO requestDTO) {
        return null;
    }
}
