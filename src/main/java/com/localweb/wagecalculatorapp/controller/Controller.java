package com.localweb.wagecalculatorapp.controller;

import com.localweb.wagecalculatorapp.payload.DTO.ResponseByDayDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByTotals;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByUserDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByWorkingDayDTO;
import com.localweb.wagecalculatorapp.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wagecalculator")
public class Controller {

    private final ServiceInterface service;

    @Autowired
    public Controller(ServiceInterface service) {
        this.service = service;
    }

    @GetMapping("/calculate/v1")
    public List<ResponseByUserDTO> calculateV1(@RequestParam String keyword,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateFrom,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateTo){
        return service.calculateV1(keyword,dateFrom,dateTo);
    }

    @GetMapping("/calculate/v2")
    public List<ResponseByDayDTO> calculateV2(){
        return service.calculateV2();
    }

    @GetMapping("/calculate/v3")
    public List<ResponseByWorkingDayDTO> calculateV3(){
        return service.calculateV3();
    }

    @GetMapping("/calculateTotals")
    public List<ResponseByTotals> calculateTotalsByUser(){
        return service.calculateTotalsByUser();
    }

}
