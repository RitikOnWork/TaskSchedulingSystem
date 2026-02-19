package com.promanage.service;

import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerServiceTest {

    private final SchedulerService scheduler = new SchedulerService();

    @Test
    void testEmptyProjectList() {
        List<Project> empty = new ArrayList<>();
        List<ScheduledProject> result = scheduler.generateOptimalSchedule(empty);
        assertTrue(result.isEmpty(), "Schedule should be empty for empty input.");
    }

    @Test
    void testSingleProject() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("P1", "Project 1", 3, 100.0));

        List<ScheduledProject> result = scheduler.generateOptimalSchedule(projects);
        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getProject().getProjectId());
    }

    @Test
    void testNonConflictingProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("P1", "Project 1", 1, 100.0));
        projects.add(new Project("P2", "Project 2", 2, 200.0));

        List<ScheduledProject> result = scheduler.generateOptimalSchedule(projects);
        assertEquals(2, result.size());
        assertEquals(300.0, scheduler.calculateTotalRevenue(result));
    }

    @Test
    void testConflictingProjects_KnapsackLogic() {
        // P1: Day 1, 100
        // P2: Day 1, 200 -> P2 should be picked
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("P1", "Project 1", 1, 100.0));
        projects.add(new Project("P2", "Project 2", 1, 200.0));

        List<ScheduledProject> result = scheduler.generateOptimalSchedule(projects);
        assertEquals(1, result.size());
        assertEquals("P2", result.get(0).getProject().getProjectId());
        assertEquals(200.0, scheduler.calculateTotalRevenue(result));
    }

    @Test
    void testComplexScenario() {
        // P1: Day 2, 50
        // P2: Day 2, 100
        // P3: Day 1, 200
        // Expected: P3 on Day 1, P2 on Day 2. Total 300.
        List<Project> projects = new ArrayList<>();
        projects.add(new Project("P1", "Conflict 1", 2, 50.0));
        projects.add(new Project("P2", "Conflict 2", 2, 100.0));
        projects.add(new Project("P3", "High Val", 1, 200.0));

        List<ScheduledProject> result = scheduler.generateOptimalSchedule(projects);
        assertEquals(2, result.size());

        // Verify total revenue
        double total = scheduler.calculateTotalRevenue(result);
        assertEquals(300.0, total);

        // Verify specific assignments
        boolean p3Found = false;
        boolean p2Found = false;

        for (ScheduledProject sp : result) {
            if (sp.getProject().getProjectId().equals("P3")) {
                assertEquals(1, sp.getDayNumber());
                p3Found = true;
            }
            if (sp.getProject().getProjectId().equals("P2")) {
                // P2 could be day 1 or 2 structurally, but mostly day 2 since P3 takes day 1
                assertEquals(2, sp.getDayNumber());
                p2Found = true;
            }
        }
        assertTrue(p3Found && p2Found);
    }
}
