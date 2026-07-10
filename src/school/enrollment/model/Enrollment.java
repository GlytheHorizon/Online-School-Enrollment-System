package school.enrollment.model;

import java.time.LocalDate;

public class Enrollment {
    private int enrollmentId;
    private String studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private String status;

    private String studentName;
    private String courseName;
    private String courseCode;
    private int units;
    private double tuitionPerUnit;

    public Enrollment() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public double getTuitionPerUnit() { return tuitionPerUnit; }
    public void setTuitionPerUnit(double tuitionPerUnit) { this.tuitionPerUnit = tuitionPerUnit; }

    public double getTotalTuition() {
        return units * tuitionPerUnit;
    }
}
