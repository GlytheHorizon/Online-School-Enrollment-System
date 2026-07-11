package school.enrollment.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import school.enrollment.dao.EnrollmentDAO;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.Enrollment;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public void insert(Enrollment enrollment) throws Exception {
        String sql = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setString(3, enrollment.getStatus() != null ? enrollment.getStatus() : "Enrolled");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) enrollment.setEnrollmentId(rs.getInt(1));
            }
        }
    }

    @Override
    public void updateStatus(int enrollmentId, String status) throws Exception {
        String sql = "UPDATE enrollments SET status=? WHERE enrollment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, enrollmentId);
            ps.executeUpdate();
        }
    }

    @Override
    public Enrollment get(int enrollmentId) throws Exception {
        String sql = "SELECT e.*, s.first_name, s.last_name, c.course_code, c.course_name, c.units, c.tuition_per_unit " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.enrollment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEnrollment(rs);
            }
        }
        return null;
    }

    @Override
    public List<Enrollment> getAll() throws Exception {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, s.first_name, s.last_name, c.course_code, c.course_name, c.units, c.tuition_per_unit " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapEnrollment(rs));
        }
        return list;
    }

    @Override
    public List<Enrollment> getByStudent(String studentId) throws Exception {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, s.first_name, s.last_name, c.course_code, c.course_name, c.units, c.tuition_per_unit " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.student_id = ? ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    @Override
    public List<Enrollment> getByCourse(int courseId) throws Exception {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, s.first_name, s.last_name, c.course_code, c.course_name, c.units, c.tuition_per_unit " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.course_id = ? ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    @Override
    public List<Enrollment> search(String keyword) throws Exception {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, s.first_name, s.last_name, c.course_code, c.course_name, c.units, c.tuition_per_unit " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR c.course_name LIKE ? OR c.course_code LIKE ? " +
                     "ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    @Override
    public boolean exists(String studentId, int courseId) throws Exception {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id=? AND course_id=? AND status='Enrolled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getString("student_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setStatus(rs.getString("status"));
        Date d = rs.getDate("enrollment_date");
        if (d != null) e.setEnrollmentDate(d.toLocalDate());
        e.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
        e.setCourseCode(rs.getString("course_code"));
        e.setCourseName(rs.getString("course_name"));
        e.setUnits(rs.getInt("units"));
        e.setTuitionPerUnit(rs.getDouble("tuition_per_unit"));
        return e;
    }
}
