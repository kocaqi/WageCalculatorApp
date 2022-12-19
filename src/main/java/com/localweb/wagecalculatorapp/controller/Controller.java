package com.localweb.wagecalculatorapp.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wagecalculator")
public class Controller {

    private final ServiceInterface service;

    @Autowired
    public Controller(ServiceInterface service) {
        this.service = service;
    }

    @GetMapping("/calculate")
    public List<ResponseDTO> calculateWage(@RequestParam String keyword,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateFrom,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateTo){
        return service.calculateWage(keyword,dateFrom,dateTo);
    }

}
