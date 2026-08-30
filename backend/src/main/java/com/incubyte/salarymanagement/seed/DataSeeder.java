package com.incubyte.salarymanagement.seed;

import com.incubyte.salarymanagement.domain.SalaryChangeReason;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final long RANDOM_SEED = 42L;
    private static final double VETERAN_EMPLOYEE_SHARE = 0.2;

    private final JdbcTemplate jdbcTemplate;
    private final int employeeCount;

    public DataSeeder(JdbcTemplate jdbcTemplate, @Value("${app.seed.employee-count:10000}") int employeeCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.employeeCount = employeeCount;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Long existingEmployees = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Long.class);
        if (existingEmployees != null && existingEmployees > 0) {
            log.info("Skipping seed: employee table already has {} rows", existingEmployees);
            return;
        }

        log.info("Seeding {} employees...", employeeCount);
        Faker faker = new Faker(new Random(RANDOM_SEED));
        Random random = new Random(RANDOM_SEED);

        List<EmployeeSeedRow> employeeRows = new ArrayList<>(employeeCount);
        List<SalaryRecordSeedRow> salaryRows = new ArrayList<>();

        List<Long> employeeIds = fetchSequenceValues("employee_id_seq", employeeCount);

        for (int i = 0; i < employeeCount; i++) {
            long employeeId = employeeIds.get(i);
            String employeeCode = "EMP-" + String.format("%05d", i + 1);
            employeeRows.add(buildEmployeeRow(faker, random, employeeId, employeeCode, salaryRows));
        }

        List<Long> salaryRecordIds = fetchSequenceValues("salary_record_id_seq", salaryRows.size());
        for (int i = 0; i < salaryRows.size(); i++) {
            salaryRows.get(i).id = salaryRecordIds.get(i);
        }

        insertEmployees(employeeRows);
        insertSalaryRecords(salaryRows);

        log.info("Seed complete: {} employees, {} salary records", employeeRows.size(), salaryRows.size());
    }

    private EmployeeSeedRow buildEmployeeRow(Faker faker, Random random, long employeeId, String employeeCode,
                                              List<SalaryRecordSeedRow> salaryRows) {
        CountryProfile countryProfile = pickRandom(SeedReferenceData.COUNTRIES, random);
        String department = pickRandom(SeedReferenceData.DEPARTMENTS, random);
        SeniorityProfile seniority = pickWeightedSeniority(random);
        String roleTitle = pickRandom(SeedReferenceData.ROLE_TITLES_BY_DEPARTMENT.get(department), random);
        String jobTitle = buildJobTitle(seniority, department, roleTitle);

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = buildEmployeeEmail(firstName, lastName, employeeId);

        boolean isVeteranEmployee = random.nextDouble() < VETERAN_EMPLOYEE_SHARE;
        LocalDate hireDate = isVeteranEmployee
                ? LocalDate.now().minusYears(2 + random.nextInt(5))
                : LocalDate.now().minusMonths(random.nextInt(24));

        BigDecimal targetSalary = computeSalaryAmount(seniority, department, countryProfile, random);
        addSalaryHistory(employeeId, hireDate, targetSalary, countryProfile.currency(), isVeteranEmployee, random, salaryRows);

        String state = faker.address().state();
        String address = faker.address().streetAddress() + ", " + faker.address().city() + " " + faker.address().zipCode();

        return new EmployeeSeedRow(employeeId, employeeCode, firstName, lastName, email, department, jobTitle,
                seniority.label(), countryProfile.country(), state, address, countryProfile.currency(), hireDate);
    }

    private void addSalaryHistory(long employeeId, LocalDate hireDate, BigDecimal targetSalary, String currency,
                                   boolean isVeteranEmployee, Random random, List<SalaryRecordSeedRow> salaryRows) {
        if (!isVeteranEmployee) {
            salaryRows.add(new SalaryRecordSeedRow(employeeId, targetSalary, currency, hireDate, null, SalaryChangeReason.HIRE));
            return;
        }

        LocalDate raiseDate = hireDate.plusYears(1 + random.nextInt(2));
        BigDecimal startingSalary = targetSalary.multiply(BigDecimal.valueOf(0.85)).setScale(2, RoundingMode.HALF_UP);
        salaryRows.add(new SalaryRecordSeedRow(employeeId, startingSalary, currency, hireDate, raiseDate.minusDays(1), SalaryChangeReason.HIRE));
        salaryRows.add(new SalaryRecordSeedRow(employeeId, targetSalary, currency, raiseDate, null, SalaryChangeReason.RAISE));
    }

    private BigDecimal computeSalaryAmount(SeniorityProfile seniority, String department, CountryProfile countryProfile,
                                            Random random) {
        int baseAmount = seniority.baseMinAmount() + random.nextInt(seniority.baseMaxAmount() - seniority.baseMinAmount() + 1);
        double departmentMultiplier = SeedReferenceData.DEPARTMENT_PAY_MULTIPLIER.getOrDefault(department, 1.0);
        double jitter = 0.9 + (random.nextDouble() * 0.2);
        double amount = baseAmount * departmentMultiplier * jitter * countryProfile.salaryMagnitudeMultiplier();
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildJobTitle(SeniorityProfile seniority, String department, String roleTitle) {
        boolean isPeopleLeaderLevel = seniority.label().equals("Manager") || seniority.label().equals("Director")
                || seniority.label().equals("VP");
        return isPeopleLeaderLevel ? seniority.label() + ", " + department : seniority.label() + " " + roleTitle;
    }

    private String buildEmployeeEmail(String firstName, String lastName, long employeeId) {
        return (firstName + "." + lastName + "." + employeeId + "@acme-corp.example").toLowerCase();
    }

    private SeniorityProfile pickWeightedSeniority(Random random) {
        List<SeniorityProfile> levels = SeedReferenceData.SENIORITY_LEVELS;
        int[] weights = {2, 20, 30, 25, 10, 8, 4, 1};
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        int pick = random.nextInt(totalWeight);
        int cumulative = 0;
        for (int i = 0; i < levels.size(); i++) {
            cumulative += weights[i];
            if (pick < cumulative) {
                return levels.get(i);
            }
        }
        return levels.get(levels.size() - 1);
    }

    private <T> T pickRandom(List<T> values, Random random) {
        return values.get(random.nextInt(values.size()));
    }

    private List<Long> fetchSequenceValues(String sequenceName, int count) {
        String sql = "SELECT nextval('" + sequenceName + "') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    private void insertEmployees(List<EmployeeSeedRow> rows) {
        String sql = """
            INSERT INTO employee (id, employee_code, first_name, last_name, email, department, job_title,
                                   seniority_level, country, state, address, currency, hire_date, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', now(), now())
            """;
        List<Object[]> batchArgs = rows.stream()
                .map(row -> new Object[]{row.id, row.employeeCode, row.firstName, row.lastName, row.email,
                        row.department, row.jobTitle, row.seniorityLevel, row.country, row.state, row.address,
                        row.currency, row.hireDate})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertSalaryRecords(List<SalaryRecordSeedRow> rows) {
        String sql = """
            INSERT INTO salary_record (id, employee_id, amount, currency, effective_from, effective_to, reason, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, now())
            """;
        List<Object[]> batchArgs = rows.stream()
                .map(row -> new Object[]{row.id, row.employeeId, row.amount, row.currency, row.effectiveFrom,
                        row.effectiveTo, row.reason.name()})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private static final class EmployeeSeedRow {
        final long id;
        final String employeeCode;
        final String firstName;
        final String lastName;
        final String email;
        final String department;
        final String jobTitle;
        final String seniorityLevel;
        final String country;
        final String state;
        final String address;
        final String currency;
        final LocalDate hireDate;

        EmployeeSeedRow(long id, String employeeCode, String firstName, String lastName, String email,
                        String department, String jobTitle, String seniorityLevel, String country, String state,
                        String address, String currency, LocalDate hireDate) {
            this.id = id;
            this.employeeCode = employeeCode;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.department = department;
            this.jobTitle = jobTitle;
            this.seniorityLevel = seniorityLevel;
            this.country = country;
            this.state = state;
            this.address = address;
            this.currency = currency;
            this.hireDate = hireDate;
        }
    }

    private static final class SalaryRecordSeedRow {
        Long id;
        final long employeeId;
        final BigDecimal amount;
        final String currency;
        final LocalDate effectiveFrom;
        final LocalDate effectiveTo;
        final SalaryChangeReason reason;

        SalaryRecordSeedRow(long employeeId, BigDecimal amount, String currency, LocalDate effectiveFrom,
                            LocalDate effectiveTo, SalaryChangeReason reason) {
            this.employeeId = employeeId;
            this.amount = amount;
            this.currency = currency;
            this.effectiveFrom = effectiveFrom;
            this.effectiveTo = effectiveTo;
            this.reason = reason;
        }
    }
}
