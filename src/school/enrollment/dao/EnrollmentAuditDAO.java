package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.EnrollmentAudit;

public interface EnrollmentAuditDAO {
    void insert(EnrollmentAudit audit) throws Exception;
    List<EnrollmentAudit> getAll() throws Exception;
    List<EnrollmentAudit> search(String keyword) throws Exception;
    List<EnrollmentAudit> getByStudent(String studentId) throws Exception;
}
