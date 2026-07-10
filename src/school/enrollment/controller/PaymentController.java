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

    public void makePayment(int enrollmentId, double amount, String paymentMethod, String referenceNumber) {
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
            p.setPaymentMethod(paymentMethod.trim());
            p.setReferenceNumber(referenceNumber.trim());
            paymentDAO.insert(p);
            JOptionPane.showMessageDialog(null, "Payment recorded successfully!\nPayment ID: " + p.getPaymentId(), "Success", JOptionPane.INFORMATION_MESSAGE);
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

    public void loadPayments(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            for (Payment p : paymentDAO.getAll()) {
                model.addRow(new Object[]{
                    p.getPaymentId(), p.getStudentName(), p.getCourseName(),
                    String.format("%.2f", p.getAmount()), p.getPaymentMethod(),
                    p.getReferenceNumber(), p.getPaymentDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchPayments(JTable table, String keyword) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            List<Payment> list = keyword.trim().isEmpty() ? paymentDAO.getAll() : paymentDAO.search(keyword.trim());
            for (Payment p : list) {
                model.addRow(new Object[]{
                    p.getPaymentId(), p.getStudentName(), p.getCourseName(),
                    String.format("%.2f", p.getAmount()), p.getPaymentMethod(),
                    p.getReferenceNumber(), p.getPaymentDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error searching payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
