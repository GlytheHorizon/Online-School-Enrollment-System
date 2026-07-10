package school.enrollment.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import school.enrollment.dao.PaymentDAO;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.Payment;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void insert(Payment payment) throws Exception {
        String sql = "INSERT INTO payments (enrollment_id, amount, payment_method, reference_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getEnrollmentId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod());
            ps.setString(4, payment.getReferenceNumber());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) payment.setPaymentId(rs.getInt(1));
            }
        }
    }

    @Override
    public void delete(int paymentId) throws Exception {
        String sql = "DELETE FROM payments WHERE payment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        }
    }

    @Override
    public Payment get(int paymentId) throws Exception {
        String sql = "SELECT p.*, s.first_name, s.last_name, c.course_name " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE p.payment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPayment(rs);
            }
        }
        return null;
    }

    @Override
    public List<Payment> getAll() throws Exception {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.first_name, s.last_name, c.course_name " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPayment(rs));
        }
        return list;
    }

    @Override
    public List<Payment> getByEnrollment(int enrollmentId) throws Exception {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.first_name, s.last_name, c.course_name " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE p.enrollment_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        }
        return list;
    }

    @Override
    public List<Payment> search(String keyword) throws Exception {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.first_name, s.last_name, c.course_name " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR p.payment_method LIKE ? OR p.reference_number LIKE ? " +
                     "ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        }
        return list;
    }

    @Override
    public double getTotalPaid(int enrollmentId) throws Exception {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE enrollment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0;
    }

    @Override
    public List<Payment> getByStudentId(String studentId) throws Exception {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.first_name, s.last_name, c.course_name " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.student_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        }
        return list;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setEnrollmentId(rs.getInt("enrollment_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setReferenceNumber(rs.getString("reference_number"));
        Date d = rs.getDate("payment_date");
        if (d != null) p.setPaymentDate(d.toLocalDate());
        p.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
        p.setCourseName(rs.getString("course_name"));
        return p;
    }
}
