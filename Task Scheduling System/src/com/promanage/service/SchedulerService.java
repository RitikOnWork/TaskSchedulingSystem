package com.promanage.service;

import com.promanage.model.Project;

import java.util.*;

public class SchedulerService {

    public void generateSchedule(List<Project> projects) {

        // Sort by revenue (descending)
        projects.sort((a, b) -> b.getRevenue() - a.getRevenue());

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        Project[] schedule = new Project[5];

        int totalRevenue = 0;

        for (Project p : projects) {
            for (int d = Math.min(5, p.getDeadline()) - 1; d >= 0; d--) {
                if (schedule[d] == null) {
                    schedule[d] = p;
                    totalRevenue += p.getRevenue();
                    break;
                }
            }
        }

        System.out.println("\n📅 Weekly Schedule:");
        for (int i = 0; i < 5; i++) {
            if (schedule[i] != null) {
                System.out.println(days[i] + " → " + schedule[i].getTitle());
            } else {
                System.out.println(days[i] + " → No Project");
            }
        }

        System.out.println("\n💰 Total Revenue: " + totalRevenue);
    }
}
