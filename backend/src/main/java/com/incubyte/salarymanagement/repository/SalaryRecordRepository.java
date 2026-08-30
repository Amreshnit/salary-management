package com.incubyte.salarymanagement.repository;

import com.incubyte.salarymanagement.model.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

    List<SalaryRecord> findByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);

    @Query("""
        SELECT s FROM SalaryRecord s
        WHERE s.employee.id = :employeeId AND s.effectiveTo IS NULL
        """)
    Optional<SalaryRecord> findCurrentByEmployeeId(@Param("employeeId") Long employeeId);
}
