package school.enrollment.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import school.enrollment.dao.CourseDAO;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.Course;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public void insert(Course course) throws Exception {
        String sql = "INSERT INTO courses (course_code, course_name, units, tuition_per_unit) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setInt(3, course.getUnits());
            ps.setDouble(4, course.getTuitionPerUnit());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) course.setCourseId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Course course) throws Exception {
        String sql = "UPDATE courses SET course_code=?, course_name=?, units=?, tuition_per_unit=? WHERE course_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setInt(3, course.getUnits());
            ps.setDouble(4, course.getTuitionPerUnit());
            ps.setInt(5, course.getCourseId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int courseId) throws Exception {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    @Override
    public Course get(int courseId) throws Exception {
        String sql = "SELECT * FROM courses WHERE course_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCourse(rs);
            }
        }
        return null;
    }

    @Override
    public List<Course> getAll() throws Exception {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCourse(rs));
        }
        return list;
    }

    @Override
    public List<Course> search(String keyword) throws Exception {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE course_code LIKE ? OR course_name LIKE ? ORDER BY course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCourse(rs));
            }
        }
        return list;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setUnits(rs.getInt("units"));
        c.setTuitionPerUnit(rs.getDouble("tuition_per_unit"));
        return c;
    }
}
