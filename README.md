# Org Analyzer

Small Java 17 console application to analyze an organization's structure.

What it does

- Reads employees from a CSV file (header: Id,firstName,lastName,salary,managerId)
- Reports managers who are underpaid (less than 20% above avg of direct reports)
- Reports managers who are overpaid (more than 50% above avg of direct reports)
- Reports employees whose reporting line to the CEO has more than 4 managers between them and the CEO

Build & run

- mvn test
- mvn package
- java -jar target/org-analyzer-1.0-SNAPSHOT.jar src/main/resources/data/employees.csv

Design notes & assumptions

- Employees are uniquely identified by the Id field (string). ManagerId references another Id.
- The CSV reader is lenient (missing managerId allowed). The first header row is skipped.
- The number of managers between employee and CEO is computed as the number of reporting hops from CEO to the employee (CEO has 0). The requirement "more than 4 managers between them and the CEO" means depth > 4.
- If CSV contains cycles or missing managers, those employees without a path from CEO will be ignored for depth checks.

Tests

- Simple JUnit tests verify parsing and analyzer behaviors.
