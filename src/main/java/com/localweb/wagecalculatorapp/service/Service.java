package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.entity.OffDay;
import com.localweb.wagecalculatorapp.entity.User;
import com.localweb.wagecalculatorapp.entity.WorkingDay;
import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.payload.DTO.UserDTO;
import com.localweb.wagecalculatorapp.repository.OffDayRepository;
import com.localweb.wagecalculatorapp.repository.UserRepository;
import com.localweb.wagecalculatorapp.repository.WorkingDayRepository;
import com.localweb.wagecalculatorapp.specification.SearchCriteria;
import com.localweb.wagecalculatorapp.specification.Specify;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
public class Service implements ServiceInterface{
    private final UserRepository userRepository;
    private final WorkingDayRepository workingDayRepository;
    private final OffDayRepository offDayRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public Service(UserRepository userRepository, WorkingDayRepository workingDayRepository, OffDayRepository offDayRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.workingDayRepository = workingDayRepository;
        this.offDayRepository = offDayRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ResponseDTO> calculateWage(RequestDTO requestDTO) {
        List<ResponseDTO> responses = new ArrayList<>();

        String keyword = requestDTO.getKeyword();
        LocalDate dateFrom = requestDTO.getDateFrom();
        LocalDate dateTo = requestDTO.getDateTo();

        Set<User> users = new HashSet<>();

        String[] keywords = keyword.split(" ");

        for (String s : keywords) {
            Specify<User> specifyByFirstName = new Specify<>(new SearchCriteria("firstName", ":", s));
            Specify<User> specifyByLastName = new Specify<>(new SearchCriteria("lastName", ":", s));
            Specify<User> specifyByEmail = new Specify<>(new SearchCriteria("email", ":", s));

            List<User> userList = userRepository.findAll(
                    Specification.where(specifyByFirstName).and(specifyByLastName).and(specifyByEmail));

            users.addAll(userList);
        }

        Specify<WorkingDay> specifyByDateFrom = new Specify<>(new SearchCriteria("date", ">", dateFrom));
        Specify<WorkingDay> specifyByDateTo = new Specify<>(new SearchCriteria("date", "<", dateTo));

        double amount;

        for (User user : users)
        {
            amount = 0;
            double hourly = user.getWage()/176.0;

            Specify<WorkingDay> specifyByUser = new Specify<>(new SearchCriteria("user", ":", user));

            List<WorkingDay> workingDays = workingDayRepository.findAll(
                    Specification.where(specifyByUser).and(specifyByDateFrom).and(specifyByDateTo));

            for(WorkingDay workingDay : workingDays)
            {
                int hours = workingDay.getHours();
                if(!isHoliday(workingDay.getDate()))
                    if(hours<=8)
                        amount+=hourly*hours;
                    else
                        amount+=8*hourly + (hours-8)*hourly*1.5;
                else
                    if(hours<=8)
                        amount+=hours*hourly*1.5;
                    else
                        amount+=8*hourly*1.5 + (hours-8)*hourly*2.0;
            }
            ResponseDTO response = new ResponseDTO();
            response.setAmount(amount);
            response.setDateFrom(dateFrom);
            response.setDateTo(dateTo);
            response.setUserDTO(modelMapper.map(user, UserDTO.class));
            responses.add(response);
        }
        return responses;
    }

    private boolean isHoliday(LocalDate date)
    {
        List<OffDay> offDays = offDayRepository.findAll();
        List<LocalDate> offDates = new ArrayList<>();
        for(OffDay offDay : offDays)
            offDates.add(offDay.getDate());
        return offDates.contains(date);
    }

}
