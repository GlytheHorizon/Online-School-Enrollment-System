package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Student;

public interface StudentDAO {
    void insert(Student student) throws Exception;
    void update(Student student) throws Exception;
    void deactivate(String studentId) throws Exception;
    Student get(String studentId) throws Exception;
    List<Student> getAll() throws Exception;
    List<Student> getAllActive() throws Exception;
    List<Student> search(String keyword) throws Exception;
    List<Student> searchActive(String keyword) throws Exception;
    List<Student> getAllInactive() throws Exception;
    List<Student> searchInactive(String keyword) throws Exception;
    void reactivate(String studentId) throws Exception;
    boolean hasActiveEnrollments(String studentId) throws Exception;
    boolean hasPayments(String studentId) throws Exception;
}
