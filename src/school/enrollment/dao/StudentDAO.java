package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Student;

public interface StudentDAO {
    void insert(Student student) throws Exception;
    void update(Student student) throws Exception;
    void delete(String studentId) throws Exception;
    Student get(String studentId) throws Exception;
    List<Student> getAll() throws Exception;
    List<Student> search(String keyword) throws Exception;
}
