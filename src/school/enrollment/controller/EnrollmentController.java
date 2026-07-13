package school.enrollment.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import school.enrollment.dao.EnrollmentDAO;
import school.enrollment.dao.EnrollmentAuditDAO;
import school.enrollment.dao.PaymentDAO;
import school.enrollment.daoimpl.EnrollmentDAOImpl;
import school.enrollment.daoimpl.EnrollmentAuditDAOImpl;
import school.enrollment.daoimpl.PaymentDAOImpl;
import school.enrollment.model.Course;
import school.enrollment.model.Enrollment;
import school.enrollment.model.EnrollmentAudit;

public class EnrollmentController {
    private final EnrollmentDAO enrollmentDAO;
    private final EnrollmentAuditDAO auditDAO;
    private final PaymentDAO paymentDAO;

    public EnrollmentController() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.auditDAO = new EnrollmentAuditDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    public void enrollStudent(String studentId, List<Course> courses) {
        if (studentId == null || studentId.trim().isEmpty() || courses == null || courses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a student and at least one course.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder successMsg = new StringBuilder("Student enrolled successfully in:\n");
        StringBuilder errorMsg = new StringBuilder();
        int successCount = 0;
        for (Course course : courses) {
            int courseId = course.getCourseId();
            try {
                if (enrollmentDAO.exists(studentId, courseId)) {
                    errorMsg.append("- ").append(course.getCourseCode()).append(": already enrolled\n");
                    continue;
                }
                Enrollment e = new Enrollment();
                e.setStudentId(studentId);
                e.setCourseId(courseId);
                e.setStatus("Enrolled");
                enrollmentDAO.insert(e);

                EnrollmentAudit audit = new EnrollmentAudit();
                audit.setStudentId(studentId);
                audit.setCourseId(courseId);
                audit.setAction("ENROLLED");
                auditDAO.insert(audit);

                successMsg.append("- ").append(course.getCourseCode()).append(" (").append(course.getCourseName()).append(")\n");
                successCount++;
            } catch (Exception ex) {
                errorMsg.append("- ").append(course.getCourseCode()).append(": ").append(ex.getMessage()).append("\n");
            }
        }
        StringBuilder fullMsg = new StringBuilder();
        if (successCount > 0) {
            fullMsg.append(successMsg);
        }
        if (errorMsg.length() > 0) {
            if (successCount > 0) fullMsg.append("\n");
            fullMsg.append("Issues:\n").append(errorMsg);
        }
        JOptionPane.showMessageDialog(null, fullMsg.toString(), successCount > 0 ? "Success" : "Errors", successCount > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void dropEnrollment(int enrollmentId) {
        if (enrollmentId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select an enrollment to drop.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double paid = paymentDAO.getTotalPaid(enrollmentId);
            if (paid > 0) {
                JOptionPane.showMessageDialog(null, "Cannot drop this enrollment — payment has already been made (P" + String.format("%.2f", paid) + ").\nContact admin for refund/reimbursement.", "Payment Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Enrollment e = enrollmentDAO.get(enrollmentId);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "Enrollment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            enrollmentDAO.updateStatus(enrollmentId, "Dropped");

            EnrollmentAudit audit = new EnrollmentAudit();
            audit.setStudentId(e.getStudentId());
            audit.setCourseId(e.getCourseId());
            audit.setAction("DROPPED");
            auditDAO.insert(audit);

            JOptionPane.showMessageDialog(null, "Enrollment dropped successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error dropping enrollment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    public void loadAuditLog(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (EnrollmentAudit a : auditDAO.getAll()) {
                model.addRow(new Object[]{
                    a.getAuditId(), a.getStudentName(), a.getCourseCode(),
                    a.getCourseName(), a.getAction(), a.getActionDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading history: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchAuditLog(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<EnrollmentAudit> list = keyword.trim().isEmpty() ? auditDAO.getAll() : auditDAO.search(keyword.trim());
            for (EnrollmentAudit a : list) {
                model.addRow(new Object[]{
                    a.getAuditId(), a.getStudentName(), a.getCourseCode(),
                    a.getCourseName(), a.getAction(), a.getActionDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error searching history: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Enrollment getEnrollment(int enrollmentId) {
        try {
            return enrollmentDAO.get(enrollmentId);
        } catch (Exception e) {
            return null;
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

    public List<Enrollment> getSearchEnrollments(String keyword) {
        try {
            return keyword.trim().isEmpty() ? enrollmentDAO.getAll() : enrollmentDAO.search(keyword.trim());
        } catch (Exception e) {
            return List.of();
        }
    }
}

