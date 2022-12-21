package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.payload.DTO.ResponseByDayDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByUserDTO;

import java.util.List;

public interface ServiceInterface {
    List<ResponseByUserDTO> calculateV1(String keyword, String dateFrom, String dateTo);

    List<ResponseByDayDTO> calculateV2();
}
