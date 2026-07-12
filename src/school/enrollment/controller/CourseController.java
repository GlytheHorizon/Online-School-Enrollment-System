package school.enrollment.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import school.enrollment.dao.CourseDAO;
import school.enrollment.daoimpl.CourseDAOImpl;
import school.enrollment.model.Course;

public class CourseController {
    private final CourseDAO courseDAO;

    public CourseController() {
        this.courseDAO = new CourseDAOImpl();
    }

    public void addCourse(String courseCode, String courseName, int units, double tuitionPerUnit) {
        if (courseCode.trim().isEmpty() || courseName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Course code and name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (units <= 0) {
            JOptionPane.showMessageDialog(null, "Units must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tuitionPerUnit <= 0) {
            JOptionPane.showMessageDialog(null, "Tuition per unit must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Course c = new Course(courseCode.trim().toUpperCase(), courseName.trim(), units, tuitionPerUnit);
            courseDAO.insert(c);
            JOptionPane.showMessageDialog(null, "Course added successfully!\nID: " + c.getCourseId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateCourse(int courseId, String courseCode, String courseName, int units, double tuitionPerUnit) {
        if (courseCode.trim().isEmpty() || courseName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Course code and name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Course c = courseDAO.get(courseId);
            if (c == null) {
                JOptionPane.showMessageDialog(null, "Course not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            c.setCourseCode(courseCode.trim().toUpperCase());
            c.setCourseName(courseName.trim());
            c.setUnits(units);
            c.setTuitionPerUnit(tuitionPerUnit);
            courseDAO.update(c);
            JOptionPane.showMessageDialog(null, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deactivateCourse(int courseId) {
        if (courseId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a course to deactivate.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (courseDAO.hasActiveEnrollments(courseId)) {
                JOptionPane.showMessageDialog(null, "Cannot deactivate this course — it has active enrollments.", "Enrollment Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking enrollments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to deactivate this course?\nIt will be hidden from active lists.", "Confirm Deactivate", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            courseDAO.deactivate(courseId);
            JOptionPane.showMessageDialog(null, "Course deactivated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deactivating course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadCourses(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (Course c : courseDAO.getAllActive()) {
                model.addRow(new Object[]{c.getCourseId(), c.getCourseCode(), c.getCourseName(), c.getUnits(), String.format("%.2f", c.getTuitionPerUnit()), String.format("%.2f", c.getTotalTuition())});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading courses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchCourses(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Course> list = keyword.trim().isEmpty() ? courseDAO.getAllActive() : courseDAO.searchActive(keyword.trim());
            for (Course c : list) {
                model.addRow(new Object[]{c.getCourseId(), c.getCourseCode(), c.getCourseName(), c.getUnits(), String.format("%.2f", c.getTuitionPerUnit()), String.format("%.2f", c.getTotalTuition())});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error searching courses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Course> getAllCourses() {
        try {
            return courseDAO.getAllActive();
        } catch (Exception e) {
            return List.of();
        }
    }
}
