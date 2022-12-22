package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.entity.OffDay;
import com.localweb.wagecalculatorapp.entity.User;
import com.localweb.wagecalculatorapp.entity.WorkingDay;
import com.localweb.wagecalculatorapp.payload.DTO.*;
import com.localweb.wagecalculatorapp.repository.OffDayRepository;
import com.localweb.wagecalculatorapp.repository.UserRepository;
import com.localweb.wagecalculatorapp.repository.WorkingDayRepository;
import com.localweb.wagecalculatorapp.specification.SearchCriteria;
import com.localweb.wagecalculatorapp.specification.Specify;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@org.springframework.stereotype.Service
public class Service implements ServiceInterface{
    private final UserRepository userRepository;
    private final WorkingDayRepository workingDayRepository;
    private final OffDayRepository offDayRepository;
    private final ModelMapper modelMapper;
    double normalIn = 1.0;
    double normalExtra = 1.25;
    double weekendIn = 1.25;
    double weekendOut = 1.5;
    double holidayIn = 1.5;
    double holidayOut = 2.0;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Autowired
    public Service(UserRepository userRepository, WorkingDayRepository workingDayRepository, OffDayRepository offDayRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.workingDayRepository = workingDayRepository;
        this.offDayRepository = offDayRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ResponseByUserDTO> calculateV1(String keyword, String dateFromArg, String dateToArg) {
        List<ResponseByUserDTO> responses = new ArrayList<>();

        /*String keyword = requestDTO.getKeyword();*/
        LocalDate dateFrom = LocalDate.parse(dateFromArg);
        LocalDate dateTo = LocalDate.parse(dateToArg);

        Set<User> users = new HashSet<>();

        String[] keywords = keyword.split(" ");

        for (String key : keywords) {
            Specify<User> specifyByFirstName = new Specify<>(new SearchCriteria("firstName", ":", key));
            Specify<User> specifyByLastName = new Specify<>(new SearchCriteria("lastName", ":", key));
            Specify<User> specifyByEmail = new Specify<>(new SearchCriteria("email", ":", key));

            List<User> userList = userRepository.findAll(
                    Specification.where(specifyByFirstName).or(specifyByLastName).or(specifyByEmail));

            users.addAll(userList);
        }

        /*Specify<WorkingDay> specifyByDateFrom = new Specify<>(new SearchCriteria("date", ">", dateFrom));
        Specify<WorkingDay> specifyByDateTo = new Specify<>(new SearchCriteria("date", "<", dateTo));*/

        for (User user : users)
        {
            double amount = 0;

            Specify<WorkingDay> specifyByUser = new Specify<>(new SearchCriteria("user", ":", user));

            List<WorkingDay> workingDays = workingDayRepository.findAll(
                    Specification.where(specifyByUser)/*.and(specifyByDateFrom).and(specifyByDateTo)*/);

            for(WorkingDay workingDay : workingDays)
                amount += calculateDailyAmount(workingDay);
            ResponseByUserDTO response = new ResponseByUserDTO();
            response.setAmount(Double.parseDouble(decimalFormat.format(amount)));
            response.setDateFrom(dateFrom);
            response.setDateTo(dateTo);
            response.setUserDTO(modelMapper.map(user, UserDTO.class));
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<ResponseByDayDTO> calculateV2() {
        // select * from working_days inner join users on users.id = working_days.user_id
        List<ResponseByDayDTO> responses = new ArrayList<>();
        List<WorkingDay> workingDays = workingDayRepository.findAll();
        Set<LocalDate> uniqueWorkingDays = new HashSet<>();
        for(WorkingDay day : workingDays)
            uniqueWorkingDays.add(day.getDate());
        for(LocalDate workingDay : uniqueWorkingDays) {
            ResponseByDayDTO response = new ResponseByDayDTO();
            double dayTotal = 0;
            List<User> users = findAllUsersThatHaveWorkedOn(workingDay);
            for(User user : users) {
                WorkingDay day = workingDayRepository.findByUserAndDate(user, workingDay);
                double userTotal = calculateDailyAmount(day);
                DailyUserDTO dailyUserDTO = new DailyUserDTO();
                dailyUserDTO.setId(user.getId());
                dailyUserDTO.setEmail(user.getEmail());
                dailyUserDTO.setFirstName(user.getFirstName());
                dailyUserDTO.setLastName(user.getLastName());
                dailyUserDTO.setDailyWage(Double.parseDouble(decimalFormat.format(userTotal)));
                response.getUsers().add(dailyUserDTO);
                dayTotal+=userTotal;
            }
            response.setDate(workingDay);
            response.setTotal(Double.parseDouble(decimalFormat.format(dayTotal)));
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<ResponseByWorkingDayDTO> calculateV3() {
        List<ResponseByWorkingDayDTO> responses = new ArrayList<>();
        List<WorkingDay> workingDays = workingDayRepository.findAll();
        for(WorkingDay workingDay : workingDays) {
            ResponseByWorkingDayDTO response = new ResponseByWorkingDayDTO();
            response.setDate(workingDay.getDate());
            response.setUser(modelMapper.map(workingDay.getUser(), UserDTO.class));
            response.setHours(workingDay.getHours());
            double amount = calculateDailyAmount(workingDay);
            response.setAmount(Double.parseDouble(decimalFormat.format(amount)));
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<ResponseByTotals> calculateTotalsByUser() {
        List<ResponseByTotals> responses = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for(User user : users){
            ResponseByTotals response = new ResponseByTotals();
            double hourlyWageOfUser = user.getWage()/176.0;
            response.setUser(modelMapper.map(user, UserDTO.class));
            int responseHoursIn = 0;
            int responseHoursOut = 0;
            int responseTotalHours = 0;
            double responseTotalAmount = 0;
            List<WorkingDay> workingDays = workingDayRepository.findAllByUser(user);
            for (WorkingDay workingDay : workingDays) {
                LocalDate date = workingDay.getDate();

                int hours = workingDay.getHours();
                responseTotalHours+=hours;

                int hoursIn;
                int hoursOut;
                if(hours<=8) {
                    hoursIn=hours;
                    hoursOut=0;
                }
                else {
                    hoursIn=8;
                    hoursOut=hours-8;
                }
                responseHoursIn+=hoursIn;
                responseHoursOut+=hoursOut;
                double hourlyInCoefficient;
                double hourlyOutCoefficient;
                if(isHoliday(date)){
                    hourlyInCoefficient = holidayIn;
                    hourlyOutCoefficient = holidayOut;
                }
                else if(date.getDayOfWeek()==DayOfWeek.SATURDAY || date.getDayOfWeek()==DayOfWeek.SUNDAY){
                    hourlyInCoefficient = weekendIn;
                    hourlyOutCoefficient = weekendOut;
                }
                else {
                    hourlyInCoefficient = normalIn;
                    hourlyOutCoefficient = normalExtra;
                }
                double amount = hourlyWageOfUser * (hoursIn * hourlyInCoefficient + hoursOut * hourlyOutCoefficient);
                responseTotalAmount+=amount;
            }
            response.setTotalHours(responseTotalHours);
            response.setHoursIn(responseHoursIn);
            response.setHoursOut(responseHoursOut);
            response.setTotalAmount(Double.parseDouble(decimalFormat.format(responseTotalAmount)));
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<ResponseByTotals> calculateTotalsByWorkingDays() {
        List<ResponseByTotals> responses = new ArrayList<>();
        List<WorkingDay> workingDays = workingDayRepository.findAll();
        for(WorkingDay workingDay : workingDays) {
            ResponseByTotals response = new ResponseByTotals();
            User user = workingDay.getUser();
            response.setUser(modelMapper.map(user, UserDTO.class));
            boolean newUser = true;
            for(ResponseByTotals r : responses) {
                if(r.getUser().equals(modelMapper.map(user, UserDTO.class))) {
                    newUser = false;
                    response = r;
                }
            }

            LocalDate date = workingDay.getDate();

            int hours = workingDay.getHours();
            response.setTotalHours(response.getTotalHours()+hours);

            int hoursIn;
            int hoursOut;
            if(hours<=8) {
                hoursIn=hours;
                hoursOut=0;
            }
            else {
                hoursIn=8;
                hoursOut=hours-8;
            }
            response.setHoursIn(response.getHoursIn()+hoursIn);
            response.setHoursOut(response.getHoursOut()+hoursOut);
            double hourlyInCoefficient;
            double hourlyOutCoefficient;
            if(isHoliday(date)){
                hourlyInCoefficient = holidayIn;
                hourlyOutCoefficient = holidayOut;
            }
            else if(date.getDayOfWeek()==DayOfWeek.SATURDAY || date.getDayOfWeek()==DayOfWeek.SUNDAY){
                hourlyInCoefficient = weekendIn;
                hourlyOutCoefficient = weekendOut;
            }
            else {
                hourlyInCoefficient = normalIn;
                hourlyOutCoefficient = normalExtra;
            }
            double amount = user.getWage()/176.0 * (hoursIn * hourlyInCoefficient + hoursOut * hourlyOutCoefficient);
            response.setTotalAmount(response.getTotalAmount()+amount);
            response.setTotalAmount(Double.parseDouble(decimalFormat.format(response.getTotalAmount())));
            if(newUser){
                responses.add(response);
            }
        }
        return responses;
    }

    private ResponseByTotals findResponseByUser(List<ResponseByTotals> responses, User user) {
        for(ResponseByTotals response : responses) {
            if(response.getUser().equals(modelMapper.map(user, UserDTO.class)))
                return response;
        }
        return new ResponseByTotals();
    }


    private boolean isHoliday(LocalDate date)
    {
        List<OffDay> offDays = offDayRepository.findAll();
        List<LocalDate> offDates = new ArrayList<>();
        for(OffDay offDay : offDays)
            offDates.add(offDay.getDate());
        return offDates.contains(date);
    }

    private List<User> findAllUsersThatHaveWorkedOn(LocalDate date) {
        List<User> users = new ArrayList<>();
        List<WorkingDay> workingDays = workingDayRepository.findAllByDate(date);
        for(WorkingDay day : workingDays) {
            users.add(day.getUser());
        }
        return users;
    }

    private double calculateDailyAmount(WorkingDay workingDay) {
        LocalDate date = workingDay.getDate();
        double hourlyWageOfUser = workingDay.getUser().getWage()/176.0;
        int hours = workingDay.getHours();
        int hoursIn;
        int hoursOut;

        if(hours <= 8){
            hoursIn = hours;
            hoursOut = 0;
        } else {
            hoursIn = 8;
            hoursOut = hours - 8;
        }

        double hourlyInCoefficient;
        double hourlyOutCoefficient;
        if(isHoliday(date)){
            hourlyInCoefficient = holidayIn;
            hourlyOutCoefficient = holidayOut;
        }
        else if(date.getDayOfWeek()==DayOfWeek.SATURDAY || date.getDayOfWeek()==DayOfWeek.SUNDAY){
            hourlyInCoefficient = weekendIn;
            hourlyOutCoefficient = weekendOut;
        }
        else {
            hourlyInCoefficient = normalIn;
            hourlyOutCoefficient = normalExtra;
        }
        return hourlyWageOfUser * (hoursIn * hourlyInCoefficient + hoursOut * hourlyOutCoefficient);
    }

}
