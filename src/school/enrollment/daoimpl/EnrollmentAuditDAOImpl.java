package school.enrollment.daoimpl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import school.enrollment.dao.EnrollmentAuditDAO;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.EnrollmentAudit;

public class EnrollmentAuditDAOImpl implements EnrollmentAuditDAO {

    @Override
    public void insert(EnrollmentAudit audit) throws Exception {
        String sql = "INSERT INTO enrollment_audit (student_id, course_id, action) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, audit.getStudentId());
            ps.setInt(2, audit.getCourseId());
            ps.setString(3, audit.getAction());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) audit.setAuditId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<EnrollmentAudit> getAll() throws Exception {
        List<EnrollmentAudit> list = new ArrayList<>();
        String sql = "SELECT a.*, s.first_name, s.last_name, c.course_code, c.course_name " +
                     "FROM enrollment_audit a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "ORDER BY a.action_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapAudit(rs));
        }
        return list;
    }

    @Override
    public List<EnrollmentAudit> search(String keyword) throws Exception {
        List<EnrollmentAudit> list = new ArrayList<>();
        String sql = "SELECT a.*, s.first_name, s.last_name, c.course_code, c.course_name " +
                     "FROM enrollment_audit a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR c.course_code LIKE ? OR c.course_name LIKE ? OR a.action LIKE ? " +
                     "ORDER BY a.action_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) ps.setString(i, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapAudit(rs));
            }
        }
        return list;
    }

    @Override
    public List<EnrollmentAudit> getByStudent(String studentId) throws Exception {
        List<EnrollmentAudit> list = new ArrayList<>();
        String sql = "SELECT a.*, s.first_name, s.last_name, c.course_code, c.course_name " +
                     "FROM enrollment_audit a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.student_id = ? ORDER BY a.action_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapAudit(rs));
            }
        }
        return list;
    }

    private EnrollmentAudit mapAudit(ResultSet rs) throws SQLException {
        EnrollmentAudit a = new EnrollmentAudit();
        a.setAuditId(rs.getInt("audit_id"));
        a.setStudentId(rs.getString("student_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setAction(rs.getString("action"));
        Timestamp ts = rs.getTimestamp("action_date");
        if (ts != null) a.setActionDate(ts.toLocalDateTime());
        a.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
        a.setCourseCode(rs.getString("course_code"));
        a.setCourseName(rs.getString("course_name"));
        return a;
    }
}
