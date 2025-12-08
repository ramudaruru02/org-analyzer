package com.company.organalyzer.io;

import com.company.organalyzer.model.Employee;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {
    @Test
    void readSample() throws Exception {
        CsvReader r = new CsvReader();
        List<Employee> list = r.readEmployees(Paths.get("src/main/resources/data/employees.csv"));
        assertTrue(list.size() > 20);
        Employee ceo = list.stream().filter(e -> e.getManagerId() == null).findFirst().orElse(null);
        assertNotNull(ceo);
    }
}
