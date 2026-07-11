package school.enrollment.model;

import java.time.LocalDateTime;

public class EnrollmentAudit {
    private int auditId;
    private String studentId;
    private int courseId;
    private String action;
    private LocalDateTime actionDate;

    private String studentName;
    private String courseName;
    private String courseCode;

    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
}
