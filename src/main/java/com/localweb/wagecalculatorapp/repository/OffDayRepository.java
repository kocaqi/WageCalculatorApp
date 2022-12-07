package com.localweb.wagecalculatorapp.repository;

import com.localweb.wagecalculatorapp.entity.OffDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OffDayRepository extends JpaRepository<OffDay, Long>, JpaSpecificationExecutor<OffDay> {
}
