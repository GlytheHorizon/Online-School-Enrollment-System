package school.enrollment.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import school.enrollment.dao.StudentDAO;
import school.enrollment.database.DatabaseConnection;
import school.enrollment.model.Student;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public void insert(Student student) throws Exception {
        String sql = "INSERT INTO students " +
                     "(student_id, first_name, last_name, email, phone, " +
                     " birth_date, birth_place, civil_status, sex, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            if (student.getBirthDate() != null)
                ps.setDate(6, Date.valueOf(student.getBirthDate()));
            else
                ps.setNull(6, Types.DATE);
            ps.setString(7, student.getBirthPlace());
            ps.setString(8, student.getCivilStatus());
            ps.setString(9, student.getSex());
            ps.setString(10, student.getAddress());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Student student) throws Exception {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, " +
                     "birth_date=?, birth_place=?, civil_status=?, sex=?, address=? " +
                     "WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            if (student.getBirthDate() != null)
                ps.setDate(5, Date.valueOf(student.getBirthDate()));
            else
                ps.setNull(5, Types.DATE);
            ps.setString(6, student.getBirthPlace());
            ps.setString(7, student.getCivilStatus());
            ps.setString(8, student.getSex());
            ps.setString(9, student.getAddress());
            ps.setString(10, student.getStudentId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deactivate(String studentId) throws Exception {
        String sql = "UPDATE students SET active=0 WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
        }
    }

    @Override
    public Student get(String studentId) throws Exception {
        String sql = "SELECT * FROM students WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        }
        return null;
    }

    @Override
    public List<Student> getAll() throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    @Override
    public List<Student> getAllActive() throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active=1 ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    @Override
    public List<Student> search(String keyword) throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students " +
                     "WHERE student_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ? " +
                     "ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p);
            ps.setString(3, p); ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStudent(rs));
            }
        }
        return list;
    }

    @Override
    public List<Student> searchActive(String keyword) throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active=1 AND (student_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ?) ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStudent(rs));
            }
        }
        return list;
    }

    @Override
    public List<Student> getAllInactive() throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active=0 ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    @Override
    public List<Student> searchInactive(String keyword) throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE active=0 AND (student_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ?) ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStudent(rs));
            }
        }
        return list;
    }

    @Override
    public void reactivate(String studentId) throws Exception {
        String sql = "UPDATE students SET active=1 WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean hasActiveEnrollments(String studentId) throws Exception {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id=? AND status='Enrolled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean hasPayments(String studentId) throws Exception {
        String sql = "SELECT COUNT(*) FROM payments p JOIN enrollments e ON p.enrollment_id = e.enrollment_id WHERE e.student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getString("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        Date bd = rs.getDate("birth_date");
        if (bd != null) s.setBirthDate(bd.toLocalDate());
        s.setBirthPlace(rs.getString("birth_place"));
        s.setCivilStatus(rs.getString("civil_status"));
        s.setSex(rs.getString("sex"));
        s.setAddress(rs.getString("address"));
        Date d = rs.getDate("registration_date");
        if (d != null) s.setRegistrationDate(d.toLocalDate());
        try { s.setActive(rs.getInt("active") == 1); } catch (SQLException ignored) {}
        return s;
    }
}
