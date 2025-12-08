package com.company.organalyzer.io;

import com.company.organalyzer.model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public List<Employee> readEmployees(Path file) throws IOException {
        List<Employee> result = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                // Id,firstName,lastName,salary,managerId
                String id = parts[0].trim();
                String firstName = parts.length > 1 ? parts[1].trim() : "";
                String lastName = parts.length > 2 ? parts[2].trim() : "";
                double salary = parts.length > 3 && !parts[3].isBlank() ? Double.parseDouble(parts[3].trim()) : 0.0;
                String managerId = parts.length > 4 ? parts[4].trim() : null;
                result.add(new Employee(id, firstName, lastName, salary, managerId));
            }
        }
        return result;
    }
}
