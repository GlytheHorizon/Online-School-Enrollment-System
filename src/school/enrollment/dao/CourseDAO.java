package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Course;

public interface CourseDAO {
    void insert(Course course) throws Exception;
    void update(Course course) throws Exception;
    void deactivate(int courseId) throws Exception;
    Course get(int courseId) throws Exception;
    List<Course> getAll() throws Exception;
    List<Course> getAllActive() throws Exception;
    List<Course> search(String keyword) throws Exception;
    List<Course> searchActive(String keyword) throws Exception;
    boolean hasActiveEnrollments(int courseId) throws Exception;
}
