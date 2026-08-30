package com.incubyte.salarymanagement.repository;

import com.incubyte.salarymanagement.domain.Employee;
import com.incubyte.salarymanagement.domain.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:department IS NULL OR e.department = :department)
          AND (:country IS NULL OR e.country = :country)
          AND (:status IS NULL OR e.status = :status)
          AND (:search IS NULL OR
               LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Employee> search(
            @Param("department") String department,
            @Param("country") String country,
            @Param("status") EmployeeStatus status,
            @Param("search") String search,
            Pageable pageable);
}
