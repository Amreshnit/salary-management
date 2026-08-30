-- Generates and inserts 10,000 synthetic employees (plus one initial HIRE
-- salary record each) directly via SQL, as an alternative to the Java-based
-- seeder (DataSeeder, active under the `seed` Spring profile). Useful if you
-- want to (re)populate the database without booting the backend at all.
--
-- Run against an already-migrated database (tables must exist -- start the
-- backend once with the default profile so Flyway creates them, then stop it
-- and run this):
--
--   psql -U postgres -d salary_management -f backend/scripts/seed-employees.sql
--
-- Safe to run more than once: it skips itself if the employee table already
-- has any rows, matching the Java seeder's behavior.

DO $$
DECLARE
    departments   text[] := ARRAY['Engineering','Product','Sales','Marketing','Human Resources',
                                   'Finance','Operations','Customer Support','Legal','IT'];
    countries     text[] := ARRAY['United States','United Kingdom','Germany','France','Canada',
                                   'Australia','Singapore','India','Japan','Brazil'];
    currencies    text[] := ARRAY['USD','GBP','EUR','EUR','CAD','AUD','SGD','INR','JPY','BRL'];
    magnitudes    numeric[] := ARRAY[1.0, 0.8, 0.9, 0.9, 1.35, 1.5, 1.35, 83.0, 150.0, 5.0];
    seniorities   text[] := ARRAY['Intern','Junior','Mid','Senior','Lead','Manager','Director','VP'];
    first_names   text[] := ARRAY['James','Mary','Robert','Patricia','John','Jennifer','Michael','Linda',
                                   'William','Elizabeth','David','Barbara','Richard','Susan','Joseph',
                                   'Jessica','Thomas','Sarah','Charles','Karen','Ravi','Priya','Wei','Yuki',
                                   'Hans','Sofia','Liam','Olivia','Noah','Emma'];
    last_names    text[] := ARRAY['Smith','Johnson','Williams','Brown','Jones','Garcia','Miller','Davis',
                                   'Rodriguez','Martinez','Hernandez','Lopez','Gonzalez','Wilson','Anderson',
                                   'Thomas','Taylor','Moore','Jackson','Martin','Sharma','Tanaka','Muller',
                                   'Silva','Nguyen','Kim','Dubois','Rossi'];

    employee_count      int := 10000;
    dept_idx            int;
    country_idx          int;
    seniority_idx        int;
    seniority_base_min   numeric;
    seniority_base_max   numeric;
    base_salary          numeric;
    employee_hire_date   date;
    new_employee_id      bigint;
    i                    int;
BEGIN
    IF (SELECT COUNT(*) FROM employee) > 0 THEN
        RAISE NOTICE 'employee table already has data -- skipping seed to avoid duplicates';
        RETURN;
    END IF;

    FOR i IN 1..employee_count LOOP
        dept_idx := 1 + floor(random() * array_length(departments, 1))::int;
        country_idx := 1 + floor(random() * array_length(countries, 1))::int;
        seniority_idx := 1 + floor(random() * array_length(seniorities, 1))::int;

        seniority_base_min := 20000 + (seniority_idx - 1) * 25000;
        seniority_base_max := seniority_base_min + 40000;
        base_salary := round(
            ((seniority_base_min + random() * (seniority_base_max - seniority_base_min))
            * magnitudes[country_idx]
            * (0.9 + random() * 0.2))::numeric,
            2
        );
        employee_hire_date := CURRENT_DATE - floor(random() * 1000)::int;

        INSERT INTO employee (
            employee_code, first_name, last_name, email, department, job_title,
            seniority_level, country, currency, hire_date, status, created_at, updated_at
        )
        VALUES (
            'EMP-' || lpad(i::text, 5, '0'),
            first_names[1 + floor(random() * array_length(first_names, 1))::int],
            last_names[1 + floor(random() * array_length(last_names, 1))::int],
            lower(
                first_names[1 + floor(random() * array_length(first_names, 1))::int] || '.' ||
                last_names[1 + floor(random() * array_length(last_names, 1))::int] || '.' ||
                i || '@acme-corp.example'
            ),
            departments[dept_idx],
            seniorities[seniority_idx] || ' ' || departments[dept_idx],
            seniorities[seniority_idx],
            countries[country_idx],
            currencies[country_idx],
            employee_hire_date,
            'ACTIVE',
            now(),
            now()
        )
        RETURNING id INTO new_employee_id;

        INSERT INTO salary_record (employee_id, amount, currency, effective_from, effective_to, reason, created_at)
        VALUES (new_employee_id, base_salary, currencies[country_idx], employee_hire_date, NULL, 'HIRE', now());
    END LOOP;

    RAISE NOTICE 'Seeded % employees with an initial salary record each', employee_count;
END $$;
