package com.promanage.service;

import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================
 *  ALGORITHM: Dynamic Programming (0/1 Knapsack over bitmask)
 * ============================================================
 *
 *  The week has 5 days (Mon–Fri). We represent which days are
 *  occupied using a 5-bit bitmask (bit 0 = Day1, bit 4 = Day5).
 *
 *  dp[mask] = maximum revenue achievable when exactly the days
 *             set in 'mask' are already filled.
 *
 *  For each project i with deadline d:
 *    Try assigning it to every free day <= d.
 *    If dp[mask | dayBit] improves, update it and record the
 *    (prevMask, projectIndex, day) for backtracking.
 *
 *  After processing all projects, find the mask with the
 *  highest dp value, then backtrack to reconstruct assignments.
 *
 *  Time  : O(2^5 * n) = O(32n)
 *  Space : O(2^5)     = O(32)
 * ============================================================
 */
public class SchedulerService {

    private static final int MAX_DAYS = 5;

    public List<ScheduledProject> generateOptimalSchedule(List<Project> projects) {

        int n = projects.size();
        if (n == 0) return new ArrayList<>();

        int totalMasks = 1 << MAX_DAYS;           // 32

        double[] dp       = new double[totalMasks];
        int[]    prevMask = new int[totalMasks];
        int[]    prevProj = new int[totalMasks];   // encodes: projectIndex * 5 + (day - 1)

        for (int i = 0; i < totalMasks; i++) prevMask[i] = -1;

        // ── DP transition ─────────────────────────────────────────────────────
        for (int pi = 0; pi < n; pi++) {
            Project proj = projects.get(pi);
            int     dl   = proj.getDeadline();

            // Traverse masks in reverse (0/1 knapsack — each project used once)
            for (int mask = totalMasks - 1; mask >= 0; mask--) {
                for (int day = 1; day <= dl && day <= MAX_DAYS; day++) {
                    int bit = 1 << (day - 1);

                    if ((mask & bit) != 0) continue;   // day already taken

                    int    newMask = mask | bit;
                    double newRev  = dp[mask] + proj.getRevenue();

                    if (newRev > dp[newMask]) {
                        dp[newMask]       = newRev;
                        prevMask[newMask] = mask;
                        prevProj[newMask] = pi * MAX_DAYS + (day - 1);
                    }
                }
            }
        }

        // ── Find best mask ────────────────────────────────────────────────────
        double bestRev  = -1;
        int    bestMask = 0;
        for (int mask = 0; mask < totalMasks; mask++) {
            if (dp[mask] > bestRev) {
                bestRev  = dp[mask];
                bestMask = mask;
            }
        }

        // ── Backtrack to reconstruct assignment ───────────────────────────────
        int[] dayAssignment = new int[n];   // dayAssignment[pi] = day (1-based); 0 = not selected
        int   cur           = bestMask;

        while (cur != 0 && prevMask[cur] != -1) {
            int encoded = prevProj[cur];
            int pi      = encoded / MAX_DAYS;
            int day     = (encoded % MAX_DAYS) + 1;
            dayAssignment[pi] = day;
            cur = prevMask[cur];
        }

        // ── Build result list ─────────────────────────────────────────────────
        List<ScheduledProject> schedule = new ArrayList<>();
        for (int pi = 0; pi < n; pi++) {
            if (dayAssignment[pi] > 0) {
                schedule.add(new ScheduledProject(projects.get(pi), dayAssignment[pi]));
            }
        }

        schedule.sort((a, b) -> Integer.compare(a.getDayNumber(), b.getDayNumber()));
        return schedule;
    }

    public double calculateTotalRevenue(List<ScheduledProject> schedule) {
        return schedule.stream()
                       .mapToDouble(sp -> sp.getProject().getRevenue())
                       .sum();
    }

    public List<Project> getUnscheduledProjects(List<Project> allProjects,
                                                 List<ScheduledProject> schedule) {
        List<String> scheduledIds = new ArrayList<>();
        for (ScheduledProject sp : schedule) scheduledIds.add(sp.getProject().getProjectId());

        List<Project> unscheduled = new ArrayList<>();
        for (Project p : allProjects) {
            if (!scheduledIds.contains(p.getProjectId())) unscheduled.add(p);
        }
        return unscheduled;
    }
}
