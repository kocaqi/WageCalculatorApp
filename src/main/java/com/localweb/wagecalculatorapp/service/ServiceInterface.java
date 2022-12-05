package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;

public interface ServiceInterface {
    ResponseDTO calculateWage(RequestDTO requestDTO);
}
