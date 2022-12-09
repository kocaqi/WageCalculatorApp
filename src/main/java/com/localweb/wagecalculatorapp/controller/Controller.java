package com.localweb.wagecalculatorapp.controller;

import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<ResponseDTO> calculateWage(@RequestParam RequestDTO requestDTO){
        return service.calculateWage(requestDTO);
    }

}
