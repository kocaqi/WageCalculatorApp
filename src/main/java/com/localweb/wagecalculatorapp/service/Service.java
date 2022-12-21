package com.localweb.wagecalculatorapp.service;

import com.localweb.wagecalculatorapp.entity.OffDay;
import com.localweb.wagecalculatorapp.entity.User;
import com.localweb.wagecalculatorapp.entity.WorkingDay;
import com.localweb.wagecalculatorapp.payload.DTO.DailyUserDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByDayDTO;
import com.localweb.wagecalculatorapp.payload.DTO.ResponseByUserDTO;
import com.localweb.wagecalculatorapp.payload.DTO.UserDTO;
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
    double normalIn = 1.0;
    double normalExtra = 1.25;
    double weekendIn = 1.25;
    double weekendOut = 1.5;
    double holidayIn = 1.5;
    double holidayOut = 2.0;

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
            double hourly = user.getWage()/176.0;

            Specify<WorkingDay> specifyByUser = new Specify<>(new SearchCriteria("user", ":", user));

            List<WorkingDay> workingDays = workingDayRepository.findAll(
                    Specification.where(specifyByUser)/*.and(specifyByDateFrom).and(specifyByDateTo)*/);

            for(WorkingDay workingDay : workingDays)
            {
                // Optimizim i pjeses breanda if else
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

                double hourlyIn;
                double hourlyOut;
                if(isHoliday(workingDay.getDate())){
                    hourlyIn = holidayIn;
                    hourlyOut = holidayOut;
                }
                else if(workingDay.getDate().getDayOfWeek()==DayOfWeek.SATURDAY || workingDay.getDate().getDayOfWeek()==DayOfWeek.SUNDAY){
                    hourlyIn = weekendIn;
                    hourlyOut = weekendOut;
                }
                else {
                    hourlyIn = normalIn;
                    hourlyOut = normalExtra;
                }
                amount += hourly * (hoursIn * hourlyIn + hoursOut * hourlyOut);
            }
            ResponseByUserDTO response = new ResponseByUserDTO();
            response.setAmount(Double.parseDouble(new DecimalFormat("0.00").format(amount)));
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
                double hourlyWageOfUser = user.getWage()/176.0;
                double userTotal;

                int hours = day.getHours();
                int hoursIn;
                int hoursOut;

                if(hours <= 8){
                    hoursIn = hours;
                    hoursOut = 0;
                } else {
                    hoursIn = 8;
                    hoursOut = hours - 8;
                }

                double hourlyInCoeff;
                double hourlyOutCoeff;
                if(isHoliday(workingDay)){
                    hourlyInCoeff = holidayIn;
                    hourlyOutCoeff = holidayOut;
                }
                else if(workingDay.getDayOfWeek()==DayOfWeek.SATURDAY || workingDay.getDayOfWeek()==DayOfWeek.SUNDAY){
                    hourlyInCoeff = weekendIn;
                    hourlyOutCoeff = weekendOut;
                }
                else {
                    hourlyInCoeff = normalIn;
                    hourlyOutCoeff = normalExtra;
                }
                userTotal = hourlyWageOfUser * (hoursIn * hourlyInCoeff + hoursOut * hourlyOutCoeff);
                DailyUserDTO dailyUserDTO = new DailyUserDTO();
                dailyUserDTO.setId(user.getId());
                dailyUserDTO.setEmail(user.getEmail());
                dailyUserDTO.setFirstName(user.getFirstName());
                dailyUserDTO.setLastName(user.getLastName());
                dailyUserDTO.setDailyWage(Double.parseDouble(new DecimalFormat("0.00").format(userTotal)));
                response.getUsers().add(dailyUserDTO);
                dayTotal+=userTotal;
            }
            response.setDate(workingDay);
            response.setTotal(Double.parseDouble(new DecimalFormat("0.00").format(dayTotal)));
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

    private List<User> findAllUsersThatHaveWorkedOn(LocalDate date) {
        List<User> users = new ArrayList<>();
        List<WorkingDay> workingDays = workingDayRepository.findAllByDate(date);
        for(WorkingDay day : workingDays) {
            users.add(day.getUser());
        }
        return users;
    }

}
