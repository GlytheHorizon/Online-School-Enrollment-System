package school.enrollment.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import school.enrollment.dao.EnrollmentDAO;
import school.enrollment.dao.PaymentDAO;
import school.enrollment.daoimpl.EnrollmentDAOImpl;
import school.enrollment.daoimpl.PaymentDAOImpl;
import school.enrollment.model.Enrollment;

public class EnrollmentController {
    private final EnrollmentDAO enrollmentDAO;
    private final PaymentDAO paymentDAO;

    public EnrollmentController() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    public void enrollStudent(String studentId, int courseId) {
        if (studentId == null || studentId.trim().isEmpty() || courseId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a student and a course.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (enrollmentDAO.exists(studentId, courseId)) {
                JOptionPane.showMessageDialog(null, "This student is already enrolled in that course.", "Duplicate Enrollment", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Enrollment e = new Enrollment();
            e.setStudentId(studentId);
            e.setCourseId(courseId);
            e.setStatus("Enrolled");
            enrollmentDAO.insert(e);
            JOptionPane.showMessageDialog(null, "Student enrolled successfully!\nEnrollment ID: " + e.getEnrollmentId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error enrolling student: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelEnrollment(int enrollmentId) {
        if (enrollmentId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select an enrollment to cancel.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null, "Cancel this enrollment?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            enrollmentDAO.delete(enrollmentId);
            JOptionPane.showMessageDialog(null, "Enrollment cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error cancelling enrollment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadEnrollments(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (Enrollment e : enrollmentDAO.getAll()) {
                double paid = paymentDAO.getTotalPaid(e.getEnrollmentId());
                double balance = e.getTotalTuition() - paid;
                model.addRow(new Object[]{
                    e.getEnrollmentId(), e.getStudentName(), e.getCourseCode(),
                    e.getCourseName(), e.getUnits(), String.format("%.2f", e.getTotalTuition()),
                    String.format("%.2f", paid), String.format("%.2f", balance),
                    e.getStatus(), e.getEnrollmentDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading enrollments: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchEnrollments(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Enrollment> list = keyword.trim().isEmpty() ? enrollmentDAO.getAll() : enrollmentDAO.search(keyword.trim());
            for (Enrollment e : list) {
                double paid = paymentDAO.getTotalPaid(e.getEnrollmentId());
                double balance = e.getTotalTuition() - paid;
                model.addRow(new Object[]{
                    e.getEnrollmentId(), e.getStudentName(), e.getCourseCode(),
                    e.getCourseName(), e.getUnits(), String.format("%.2f", e.getTotalTuition()),
                    String.format("%.2f", paid), String.format("%.2f", balance),
                    e.getStatus(), e.getEnrollmentDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error searching enrollments: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentId) {
        try {
            return enrollmentDAO.getByStudent(studentId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Enrollment> getAllEnrollments() {
        try {
            return enrollmentDAO.getAll();
        } catch (Exception e) {
            return List.of();
        }
    }

    public double getTotalPaid(int enrollmentId) {
        try {
            return paymentDAO.getTotalPaid(enrollmentId);
        } catch (Exception e) {
            return 0;
        }
    }
}
