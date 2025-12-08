package com.company.organalyzer.service;

import com.company.organalyzer.model.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class OrgAnalyzer {
    private final Map<String, Employee> employeesById;

    public OrgAnalyzer(Collection<Employee> employees) {
        this.employeesById = new HashMap<>();
        for (Employee e : employees) employeesById.put(e.getId(), e);
        // build tree
        for (Employee e : employees) {
            if (e.getManagerId() != null) {
                Employee m = employeesById.get(e.getManagerId());
                if (m != null) m.addDirectReport(e);
            }
        }
    }

    public Optional<Employee> findCEO() {
        return employeesById.values().stream().filter(e -> e.getManagerId() == null).findFirst();
    }

    public Map<Employee, Double> managersUnderpaid(double minFactor) {
        Map<Employee, Double> res = new LinkedHashMap<>();
        for (Employee m : employeesById.values()) {
            List<Employee> dr = m.getDirectReports();
            if (dr.isEmpty()) continue;
            double avg = dr.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
            double minSalary = avg * (1.0 + minFactor);
            if (m.getSalary() < minSalary) res.put(m, minSalary - m.getSalary());
        }
        return res;
    }

    public Map<Employee, Double> managersOverpaid(double maxFactor) {
        Map<Employee, Double> res = new LinkedHashMap<>();
        for (Employee m : employeesById.values()) {
            List<Employee> dr = m.getDirectReports();
            if (dr.isEmpty()) continue;
            double avg = dr.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
            double maxSalary = avg * (1.0 + maxFactor);
            if (m.getSalary() > maxSalary) res.put(m, m.getSalary() - maxSalary);
        }
        return res;
    }

    public Map<Employee, Integer> employeesWithLongReportingLine(int maxManagers) {
        Map<Employee, Integer> res = new LinkedHashMap<>();
        Optional<Employee> ceoOpt = findCEO();
        if (ceoOpt.isEmpty()) return res;
        Employee ceo = ceoOpt.get();
        // compute depth (number of managers between employee and CEO)
        Map<String, Integer> depth = new HashMap<>();
        // CEO depth = 0 managers between
        depth.put(ceo.getId(), 0);
        // BFS
        Queue<Employee> q = new ArrayDeque<>();
        q.add(ceo);
        while (!q.isEmpty()) {
            Employee cur = q.poll();
            int curDepth = depth.getOrDefault(cur.getId(), 0);
            for (Employee r : cur.getDirectReports()) {
                depth.put(r.getId(), curDepth + 1);
                q.add(r);
            }
        }
        for (Employee e : employeesById.values()) {
            int managersBetween = depth.getOrDefault(e.getId(), Integer.MAX_VALUE);
            if (managersBetween > maxManagers) res.put(e, managersBetween - maxManagers);
        }
        return res.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a,b)->a, LinkedHashMap::new));
    }
}
