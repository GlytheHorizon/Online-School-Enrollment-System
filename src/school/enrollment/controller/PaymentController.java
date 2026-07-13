package school.enrollment.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import school.enrollment.dao.PaymentDAO;
import school.enrollment.daoimpl.PaymentDAOImpl;
import school.enrollment.model.Payment;

public class PaymentController {
    private final PaymentDAO paymentDAO;

    public PaymentController() {
        this.paymentDAO = new PaymentDAOImpl();
    }

    public void makePayment(int enrollmentId, double amount, double excess, String paymentMethod, String transactionId) {
        if (enrollmentId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select an enrollment.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a payment method.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Payment p = new Payment();
            p.setEnrollmentId(enrollmentId);
            p.setAmount(amount);
            p.setExcessAmount(excess);
            p.setPaymentMethod(paymentMethod.trim());
            p.setReferenceNumber(transactionId);
            paymentDAO.insert(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error recording payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePayment(int paymentId) {
        if (paymentId <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a payment to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null, "Delete this payment record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            paymentDAO.delete(paymentId);
            JOptionPane.showMessageDialog(null, "Payment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Payment> aggregatePayments(List<Payment> payments) {
        java.util.Map<String, Payment> grouped = new java.util.LinkedHashMap<>();
        for (Payment p : payments) {
            String key = p.getStudentName() + "_" + p.getReferenceNumber() + "_" + p.getPaymentMethod() + "_" + p.getPaymentDate();
            if (grouped.containsKey(key)) {
                Payment existing = grouped.get(key);
                existing.setAmount(existing.getAmount() + p.getAmount());
                existing.setExcessAmount(existing.getExcessAmount() + p.getExcessAmount());
            } else {
                Payment clone = new Payment();
                clone.setPaymentId(p.getPaymentId());
                clone.setEnrollmentId(p.getEnrollmentId());
                clone.setStudentId(p.getStudentId());
                clone.setStudentName(p.getStudentName());
                clone.setReferenceNumber(p.getReferenceNumber());
                clone.setPaymentMethod(p.getPaymentMethod());
                clone.setPaymentDate(p.getPaymentDate());
                clone.setAmount(p.getAmount());
                clone.setExcessAmount(p.getExcessAmount());
                grouped.put(key, clone);
            }
        }
        return new java.util.ArrayList<>(grouped.values());
    }

    public void loadPayments(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Payment> aggregated = aggregatePayments(paymentDAO.getAll());
            for (Payment p : aggregated) {
                model.addRow(new Object[]{
                    p.getStudentId(),
                    p.getStudentName(),
                    String.format("%.2f", p.getAmount()),
                    String.format("%.2f", p.getExcessAmount()),
                    p.getPaymentMethod(),
                    p.getReferenceNumber(),
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Payment> getAllPayments() {
        try {
            return paymentDAO.getAll();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Payment> getAllPaymentsByStudent(String studentId) {
        try {
            return paymentDAO.getByStudentId(studentId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void searchPayments(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Payment> list = keyword.trim().isEmpty() ? paymentDAO.getAll() : paymentDAO.search(keyword.trim());
            List<Payment> aggregated = aggregatePayments(list);
            for (Payment p : aggregated) {
                model.addRow(new Object[]{
                    p.getStudentId(),
                    p.getStudentName(),
                    String.format("%.2f", p.getAmount()),
                    String.format("%.2f", p.getExcessAmount()),
                    p.getPaymentMethod(),
                    p.getReferenceNumber(),
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error searching payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
