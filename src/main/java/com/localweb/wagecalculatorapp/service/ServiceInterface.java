package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;

import java.util.List;

public interface ServiceInterface {
    List<ResponseDTO> calculateWage(RequestDTO requestDTO);
}
