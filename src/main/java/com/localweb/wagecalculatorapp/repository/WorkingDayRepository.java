package com.localweb.wagecalculatorapp.repository;

import com.localweb.wagecalculatorapp.entity.WorkingDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingDayRepository extends JpaRepository<WorkingDay, Long> {
}
