package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.entity.OffDay;
import com.localweb.wagecalculatorapp.entity.User;
import com.localweb.wagecalculatorapp.entity.WorkingDay;
import com.localweb.wagecalculatorapp.payload.DTO.RequestDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseDTO;
import com.localweb.wagecalculatorapp.repository.OffDayRepository;
import com.localweb.wagecalculatorapp.repository.UserRepository;
import com.localweb.wagecalculatorapp.repository.WorkingDayRepository;
import com.localweb.wagecalculatorapp.specification.SearchCriteria;
import com.localweb.wagecalculatorapp.specification.Specify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.text.html.HTMLDocument;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        int amount;

        for (User user : users)
        {
            amount = 0;

            Specify<WorkingDay> specifyByUser = new Specify<>(new SearchCriteria("user", ":", user));
            Specify<WorkingDay> specifyByDateFrom = new Specify<>(new SearchCriteria("date", ">", dateFrom));
            Specify<WorkingDay> specifyByDateTo = new Specify<>(new SearchCriteria("date", "<", dateFrom));

            List<WorkingDay> workingDays = workingDayRepository.findAll(
                    Specification.where(specifyByUser).and(specifyByDateFrom).and(specifyByDateTo));

            List<LocalDate> dates = new ArrayList<>();
            for(WorkingDay workingDay : workingDays)
                dates.add(workingDay.getDate());
            for(LocalDate date : dates)
            {

            }
        }

        return null;
    }

    private boolean isHoliday(WorkingDay workingDay)
    {
        LocalDate workDate = workingDay.getDate();
        List<OffDay> offDays = offDayRepository.findAll();
        List<LocalDate> offDates = new ArrayList<>();
        for(OffDay offDay : offDays)
            offDates.add(offDay.getDate());
        return offDates.contains(workDate);
    }

}
