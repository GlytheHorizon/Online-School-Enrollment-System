package school.enrollment.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.Payment;

public class DashboardController {

    public int getTotalStudents() {
        return queryInt("SELECT COUNT(*) FROM students WHERE active = 1");
    }

    public int getNewStudentsThisMonth() {
        return queryInt("SELECT COUNT(*) FROM students WHERE active = 1 " +
            "AND MONTH(registration_date) = MONTH(CURDATE()) AND YEAR(registration_date) = YEAR(CURDATE())");
    }

    public int getTotalCourses() {
        return queryInt("SELECT COUNT(*) FROM courses WHERE active = 1");
    }

    public int getTotalEnrollments() {
        return queryInt("SELECT COUNT(*) FROM enrollments WHERE status = 'Enrolled'");
    }

    public int getNewEnrollmentsThisMonth() {
        return queryInt("SELECT COUNT(*) FROM enrollments WHERE status = 'Enrolled' " +
            "AND MONTH(enrollment_date) = MONTH(CURDATE()) AND YEAR(enrollment_date) = YEAR(CURDATE())");
    }

    public double getTotalRevenue() {
        return queryDouble("SELECT COALESCE(SUM(p.amount), 0) FROM payments p " +
            "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
            "JOIN students s ON e.student_id = s.student_id WHERE s.active = 1");
    }

    public double getRevenueThisMonth() {
        return queryDouble("SELECT COALESCE(SUM(p.amount), 0) FROM payments p " +
            "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
            "JOIN students s ON e.student_id = s.student_id WHERE s.active = 1 " +
            "AND MONTH(p.payment_date) = MONTH(CURDATE()) AND YEAR(p.payment_date) = YEAR(CURDATE())");
    }

    public double getTotalOutstandingBalance() {
        String sql = "SELECT COALESCE(SUM(e.total_tuition), 0) - COALESCE(SUM(paid.amt), 0) " +
            "FROM enrollments e " +
            "JOIN students s ON e.student_id = s.student_id " +
            "LEFT JOIN (SELECT enrollment_id, SUM(amount) amt FROM payments GROUP BY enrollment_id) paid " +
            "ON e.enrollment_id = paid.enrollment_id " +
            "WHERE s.active = 1 AND e.status = 'Enrolled'";
        return queryDouble(sql);
    }

    /** Last 6 months of enrollment counts, oldest to newest, e.g. {"Feb"=12, "Mar"=18, ...} */
     public Map<String, Integer> getEnrollmentTrends() {
        return getMonthlyMap(
            "SELECT DATE_FORMAT(enrollment_date, '%Y-%m') ym, COUNT(*) cnt " +
            "FROM enrollments WHERE enrollment_date IS NOT NULL GROUP BY ym");
    }

    /** Last 6 months of revenue, oldest to newest — always 6 entries. */
    public Map<String, Double> getRevenueOverview() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(p.payment_date, '%Y-%m') ym, COALESCE(SUM(p.amount),0) total " +
                     "FROM payments p JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "WHERE s.active = 1 AND p.payment_date IS NOT NULL GROUP BY ym";
        Map<String, Double> byYm = new java.util.HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) byYm.put(rs.getString("ym"), rs.getDouble("total"));
        } catch (Exception e) { /* ignore */ }

        java.time.YearMonth now = java.time.YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth ym = now.minusMonths(i);
            String key = ym.toString(); // yyyy-MM
            String label = ym.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
            result.put(label, byYm.getOrDefault(key, 0.0));
        }
        return result;
    }

    /** Shared helper: builds a fixed 6-month window (oldest→newest), filling gaps with 0. */
    private Map<String, Integer> getMonthlyMap(String groupedSql) {
        Map<String, Integer> byYm = new java.util.HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(groupedSql)) {
            while (rs.next()) byYm.put(rs.getString("ym"), rs.getInt("cnt"));
        } catch (Exception e) { /* ignore */ }

        Map<String, Integer> result = new LinkedHashMap<>();
        java.time.YearMonth now = java.time.YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth ym = now.minusMonths(i);
            String key = ym.toString();
            String label = ym.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
            result.put(label, byYm.getOrDefault(key, 0));
        }
        return result;
    }

    public List<Payment> getRecentPayments(int limit) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.payment_id, p.enrollment_id, p.amount, p.payment_method, " +
                     "       p.reference_number, p.payment_date, " +
                     "       s.first_name, s.last_name, c.course_name, e.student_id AS sid " +
                     "FROM payments p " +
                     "JOIN enrollments e ON p.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE s.active = 1 " +
                     "ORDER BY p.payment_date DESC, p.payment_id DESC " +
                     "LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    p.setStudentId(rs.getString("sid"));
                    list.add(p);
                }
            }
        } catch (Exception e) { /* ignore */ }
        return list;
    }

    private int queryInt(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { /* ignore */ }
        return 0;
    }

    private double queryDouble(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { /* ignore */ }
        return 0;
    }
}