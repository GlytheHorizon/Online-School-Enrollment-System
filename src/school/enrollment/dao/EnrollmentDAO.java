package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Enrollment;

public interface EnrollmentDAO {
    void insert(Enrollment enrollment) throws Exception;
    void updateStatus(int enrollmentId, String status) throws Exception;
    Enrollment get(int enrollmentId) throws Exception;
    List<Enrollment> getAll() throws Exception;
    List<Enrollment> getByStudent(String studentId) throws Exception;
    List<Enrollment> getByCourse(int courseId) throws Exception;
    List<Enrollment> search(String keyword) throws Exception;
    boolean exists(String studentId, int courseId) throws Exception;
}
