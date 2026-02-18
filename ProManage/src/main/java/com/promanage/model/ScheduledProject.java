package com.promanage.model;

public class ScheduledProject {

    private int     scheduleId;
    private Project project;
    private int     dayNumber;
    private String  dayName;

    private static final String[] DAY_NAMES =
            { "", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };

    public ScheduledProject() {}

    public ScheduledProject(Project project, int dayNumber) {
        this.project   = project;
        this.dayNumber = dayNumber;
        this.dayName   = (dayNumber >= 1 && dayNumber <= 5) ? DAY_NAMES[dayNumber] : "Unknown";
    }

    public int     getScheduleId()               { return scheduleId; }
    public void    setScheduleId(int id)         { this.scheduleId = id; }

    public Project getProject()                  { return project; }
    public void    setProject(Project p)         { this.project = p; }

    public int     getDayNumber()                { return dayNumber; }
    public void    setDayNumber(int d)           { this.dayNumber = d; }

    public String  getDayName()                  { return dayName; }
    public void    setDayName(String name)       { this.dayName = name; }

    @Override
    public String toString() {
        return String.format("  Day %d (%s) -> [%s] %-40s | Deadline: Day %-2d | Revenue: Rs.%,.2f",
                dayNumber, dayName,
                project.getProjectId(), project.getTitle(),
                project.getDeadline(), project.getRevenue());
    }
}