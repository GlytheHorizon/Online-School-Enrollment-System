package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Course;

public interface CourseDAO {
    void insert(Course course) throws Exception;
    void update(Course course) throws Exception;
    void delete(int courseId) throws Exception;
    Course get(int courseId) throws Exception;
    List<Course> getAll() throws Exception;
    List<Course> search(String keyword) throws Exception;
}
