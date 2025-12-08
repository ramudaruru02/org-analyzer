package com.company.organalyzer.service;

import com.company.organalyzer.io.CsvReader;
import com.company.organalyzer.model.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrgAnalyzerTest {
    static List<Employee> employees;

    @BeforeAll
    static void setup() throws Exception {
        employees = new CsvReader().readEmployees(Paths.get("src/main/resources/data/employees.csv"));
    }

    @Test
    void salaryChecks() {
        OrgAnalyzer a = new OrgAnalyzer(employees);
        Map<Employee, Double> under = a.managersUnderpaid(0.20);
        Map<Employee, Double> over = a.managersOverpaid(0.50);
        assertNotNull(under);
        assertNotNull(over);
        // basic sanity: CEO exists
        assertTrue(a.findCEO().isPresent());
    }

    @Test
    void depthChecks() {
        OrgAnalyzer a = new OrgAnalyzer(employees);
        Map<Employee, Integer> longLines = a.employeesWithLongReportingLine(4);
        assertNotNull(longLines);
    }
}
