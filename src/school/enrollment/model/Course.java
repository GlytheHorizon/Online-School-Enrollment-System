package school.enrollment.model;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int units;
    private double tuitionPerUnit;
    private boolean active = true;

    public Course() {}

    public Course(String courseCode, String courseName, int units, double tuitionPerUnit) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.units = units;
        this.tuitionPerUnit = tuitionPerUnit;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public double getTuitionPerUnit() { return tuitionPerUnit; }
    public void setTuitionPerUnit(double tuitionPerUnit) { this.tuitionPerUnit = tuitionPerUnit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getTotalTuition() {
        return units * tuitionPerUnit;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + units + " units, P" + String.format("%.2f", getTotalTuition()) + ")";
    }
}
