package com.company.organalyzer;

import com.company.organalyzer.io.CsvReader;
import com.company.organalyzer.model.Employee;
import com.company.organalyzer.service.OrgAnalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws IOException {
        // Default to sample data in classpath if no arg provided; otherwise use
        // provided path
        Path csv;
        if (args.length > 0) {
            csv = Paths.get(args[0]);
        } else {
            // Try classpath resource first (when running from jar)
            var resource = App.class.getClassLoader().getResource("data/employees.csv");
            if (resource != null) {
                try {
                    csv = Paths.get(resource.toURI());
                } catch (Exception e) {
                    // Fallback to filesystem if URI conversion fails
                    csv = Paths.get("src/main/resources/data/employees.csv");
                }
            } else {
                // Fallback to filesystem (when running from IDE or with explicit path)
                csv = Paths.get("src/main/resources/data/employees.csv");
            }
        }
        CsvReader reader = new CsvReader();
        List<Employee> employees = reader.readEmployees(csv);
        OrgAnalyzer analyzer = new OrgAnalyzer(employees);

        System.out.println("Found employees: " + employees.size());

        System.out.println("\nManagers underpaid (should earn at least 20% more than avg of direct reports):");
        Map<Employee, Double> under = analyzer.managersUnderpaid(0.20);
        if (under.isEmpty())
            System.out.println("  none");
        under.forEach((m, d) -> System.out.printf("  %s -> under by %.2f%n", m, d));

        System.out.println("\nManagers overpaid (should earn no more than 50% more than avg of direct reports):");
        Map<Employee, Double> over = analyzer.managersOverpaid(0.50);
        if (over.isEmpty())
            System.out.println("  none");
        over.forEach((m, d) -> System.out.printf("  %s -> over by %.2f%n", m, d));

        System.out.println("\nEmployees with reporting line longer than 4 managers to CEO:");
        Map<Employee, Integer> longLines = analyzer.employeesWithLongReportingLine(4);
        if (longLines.isEmpty())
            System.out.println("  none");
        longLines.forEach((e, extra) -> System.out.printf("  %s -> too long by %d managers%n", e, extra));
    }
}
