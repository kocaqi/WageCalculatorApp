package com.localweb.wagecalculatorapp.repository;

import com.localweb.wagecalculatorapp.entity.User;
import com.localweb.wagecalculatorapp.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface WorkingDayRepository extends JpaRepository<WorkingDay, Long>, JpaSpecificationExecutor<WorkingDay> {

    WorkingDay findByUserAndDate(User user, LocalDate date);

    List<WorkingDay> findAllByDate(LocalDate date);
}
