package com.incubyte.salarymanagement.seed;

import java.util.List;
import java.util.Map;

public final class SeedReferenceData {

    public static final List<CountryProfile> COUNTRIES = List.of(
            new CountryProfile("United States", "USD", 1.0),
            new CountryProfile("United Kingdom", "GBP", 0.8),
            new CountryProfile("Germany", "EUR", 0.9),
            new CountryProfile("France", "EUR", 0.9),
            new CountryProfile("Canada", "CAD", 1.35),
            new CountryProfile("Australia", "AUD", 1.5),
            new CountryProfile("Singapore", "SGD", 1.35),
            new CountryProfile("India", "INR", 83.0),
            new CountryProfile("Japan", "JPY", 150.0),
            new CountryProfile("Brazil", "BRL", 5.0)
    );

    public static final List<String> DEPARTMENTS = List.of(
            "Engineering", "Product", "Sales", "Marketing", "Human Resources",
            "Finance", "Operations", "Customer Support", "Legal", "IT"
    );

    public static final List<SeniorityProfile> SENIORITY_LEVELS = List.of(
            new SeniorityProfile("Intern", 20000, 30000),
            new SeniorityProfile("Junior", 40000, 65000),
            new SeniorityProfile("Mid", 65000, 95000),
            new SeniorityProfile("Senior", 95000, 130000),
            new SeniorityProfile("Lead", 130000, 160000),
            new SeniorityProfile("Manager", 140000, 180000),
            new SeniorityProfile("Director", 180000, 240000),
            new SeniorityProfile("VP", 240000, 320000)
    );

    public static final Map<String, List<String>> ROLE_TITLES_BY_DEPARTMENT = Map.ofEntries(
            Map.entry("Engineering", List.of("Software Engineer", "QA Engineer", "DevOps Engineer", "Data Engineer")),
            Map.entry("Product", List.of("Product Manager", "Product Analyst", "UX Designer")),
            Map.entry("Sales", List.of("Account Executive", "Sales Representative", "Sales Engineer")),
            Map.entry("Marketing", List.of("Marketing Specialist", "Content Strategist", "Growth Marketer")),
            Map.entry("Human Resources", List.of("HR Business Partner", "Recruiter", "People Operations Specialist")),
            Map.entry("Finance", List.of("Financial Analyst", "Accountant", "Controller")),
            Map.entry("Operations", List.of("Operations Analyst", "Program Manager", "Supply Chain Coordinator")),
            Map.entry("Customer Support", List.of("Support Engineer", "Customer Success Manager", "Support Specialist")),
            Map.entry("Legal", List.of("Legal Counsel", "Compliance Analyst", "Contracts Manager")),
            Map.entry("IT", List.of("Systems Administrator", "IT Support Specialist", "Network Engineer"))
    );

    public static final Map<String, Double> DEPARTMENT_PAY_MULTIPLIER = Map.ofEntries(
            Map.entry("Engineering", 1.10),
            Map.entry("Product", 1.08),
            Map.entry("Sales", 1.05),
            Map.entry("Marketing", 1.0),
            Map.entry("Human Resources", 0.95),
            Map.entry("Finance", 1.02),
            Map.entry("Operations", 0.95),
            Map.entry("Customer Support", 0.90),
            Map.entry("Legal", 1.05),
            Map.entry("IT", 1.0)
    );

    private SeedReferenceData() {
    }
}
