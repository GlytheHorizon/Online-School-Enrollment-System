package school.enrollment.dao;

import java.util.List;
import school.enrollment.model.Payment;

public interface PaymentDAO {
    void insert(Payment payment) throws Exception;
    void delete(int paymentId) throws Exception;
    Payment get(int paymentId) throws Exception;
    List<Payment> getAll() throws Exception;
    List<Payment> getByEnrollment(int enrollmentId) throws Exception;
    List<Payment> search(String keyword) throws Exception;
    double getTotalPaid(int enrollmentId) throws Exception;
    List<Payment> getByStudentId(String studentId) throws Exception;
}
