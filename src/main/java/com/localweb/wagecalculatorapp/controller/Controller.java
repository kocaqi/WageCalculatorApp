package com.localweb.wagecalculatorapp.controller;

import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wagecalculator")
public class Controller {

    private ServiceInterface service;

    @Autowired
    public Controller(ServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> calculateWage(@RequestParam RequestDTO requestDTO){
        return new ResponseEntity<>(service.calculateWage(requestDTO), HttpStatus.OK);
    }

}
