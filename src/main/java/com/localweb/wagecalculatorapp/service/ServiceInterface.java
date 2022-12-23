package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.payload.DTO.*;

import java.util.Collection;
import java.util.List;

public interface ServiceInterface {
    List<ResponseByUserDTO> calculateV1(String keyword, String dateFrom, String dateTo);

    List<ResponseByDayDTO> calculateV2();

    List<ResponseByWorkingDayDTO> calculateV3();

    List<ResponseByTotals> calculateTotalsByUser();

    List<ResponseByTotals> calculateTotalsByWorkingDays();

    Collection<ResponseByTotals> calculateTotalsUsingHashMaps();

    Collection<ResponseByMonths> calculateTotalsByMonths();
}
