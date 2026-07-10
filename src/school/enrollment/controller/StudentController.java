package school.enrollment.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import school.enrollment.dao.StudentDAO;
import school.enrollment.daoimpl.StudentDAOImpl;
import school.enrollment.model.Student;

public class StudentController {
    private final StudentDAO studentDAO;

    public StudentController() {
        this.studentDAO = new StudentDAOImpl();
    }

    public void registerStudent(String studentId, String firstName, String lastName, String email, String phone, String address) {
        if (studentId.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Student ID, first name, last name, and email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!studentId.matches("^\\d{4}-\\d{4}$")) {
            JOptionPane.showMessageDialog(null, "Student ID must be in format XXXX-XXXX (e.g., 2024-0001).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(null, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Student s = new Student(studentId.trim(), firstName.trim(), lastName.trim(), email.trim(), phone.trim(), address.trim());
            studentDAO.insert(s);
            JOptionPane.showMessageDialog(null, "Student registered successfully!\nID: " + s.getStudentId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error registering student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateStudent(String studentId, String firstName, String lastName, String email, String phone, String address) {
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "First name, last name, and email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Student s = studentDAO.get(studentId);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            s.setFirstName(firstName.trim());
            s.setLastName(lastName.trim());
            s.setEmail(email.trim());
            s.setPhone(phone.trim());
            s.setAddress(address.trim());
            studentDAO.update(s);
            JOptionPane.showMessageDialog(null, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            studentDAO.delete(studentId);
            JOptionPane.showMessageDialog(null, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadStudents(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (Student s : studentDAO.getAll()) {
                model.addRow(new Object[]{s.getStudentId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getPhone(), s.getAddress()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchStudents(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Student> list = keyword.trim().isEmpty() ? studentDAO.getAll() : studentDAO.search(keyword.trim());
            for (Student s : list) {
                model.addRow(new Object[]{s.getStudentId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getPhone(), s.getAddress()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error searching students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Student getStudent(String studentId) {
        try {
            return studentDAO.get(studentId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Student> getAllStudents() {
        try {
            return studentDAO.getAll();
        } catch (Exception e) {
            return List.of();
        }
    }
}
