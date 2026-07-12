package school.enrollment.controller;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Reference number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
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

    public String processPayments(List<double[]> enrollmentPayments, String paymentMethod, String referenceNumber) {
        StringBuilder result = new StringBuilder();
        for (double[] ep : enrollmentPayments) {
            int enrollmentId = (int) ep[0];
            double amount = ep[1];
            try {
                Payment p = new Payment();
                p.setEnrollmentId(enrollmentId);
                p.setAmount(amount);
                p.setPaymentMethod(paymentMethod.trim());
                p.setReferenceNumber(referenceNumber.trim());
                paymentDAO.insert(p);
                result.append("  P").append(String.format("%.2f", amount)).append(" - OK\n");
            } catch (Exception e) {
                result.append("  Enroll #").append(enrollmentId).append(": ").append(e.getMessage()).append("\n");
            }
        }
        return result.toString();
    }

    public void deletePayment(int paymentId) {
        if (paymentId <= 0) return;
        try {
            paymentDAO.delete(paymentId);
            JOptionPane.showMessageDialog(null, "Payment record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePaymentsByReference(String referenceNumber) {
        try {
            List<Payment> payments = paymentDAO.getByReference(referenceNumber);
            if (payments.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No payments found with that reference number.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            paymentDAO.deleteByReference(referenceNumber);
            JOptionPane.showMessageDialog(null, payments.size() + " payment record(s) deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int deletePaymentWithConfirmation(JComponent parent, int row, String studentName, String amount, String method, String ref) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("<html><b>Delete Payment Record</b><br>Type the reference number and type \"confirm\" to delete.</html>"), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Reference #:"), gbc);
        gbc.gridx = 1;
        JTextField txtRef = new JTextField(15);
        panel.add(txtRef, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Type \"confirm\":"), gbc);
        gbc.gridx = 1;
        JTextField txtConfirm = new JTextField(15);
        panel.add(txtConfirm, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(new JLabel("Record: " + studentName + ", P" + amount + " (" + method + ")", SwingConstants.CENTER), gbc);

        int option = JOptionPane.showConfirmDialog(parent, panel, "Confirm Deletion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return -1;

        String enteredRef = txtRef.getText().trim();
        String enteredConfirm = txtConfirm.getText().trim();
        if (!enteredRef.equals(ref.trim())) {
            JOptionPane.showMessageDialog(parent, "Reference number does not match. Deletion cancelled.", "Mismatch", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        if (!enteredConfirm.equalsIgnoreCase("confirm")) {
            JOptionPane.showMessageDialog(parent, "Please type \"confirm\" to proceed with deletion.", "Confirmation Required", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return row;
    }

    public List<Payment> aggregatePayments(List<Payment> payments) {
        java.util.Map<String, Payment> grouped = new java.util.LinkedHashMap<>();
        for (Payment p : payments) {
            String key = p.getStudentName() + "_" + p.getReferenceNumber() + "_" + p.getPaymentMethod() + "_" + p.getPaymentDate();
            if (grouped.containsKey(key)) {
                Payment existing = grouped.get(key);
                existing.setAmount(existing.getAmount() + p.getAmount());
            } else {
                Payment clone = new Payment();
                clone.setPaymentId(p.getPaymentId());
                clone.setEnrollmentId(p.getEnrollmentId());
                clone.setStudentName(p.getStudentName());
                clone.setReferenceNumber(p.getReferenceNumber());
                clone.setPaymentMethod(p.getPaymentMethod());
                clone.setPaymentDate(p.getPaymentDate());
                clone.setAmount(p.getAmount());
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
                    p.getStudentName(),
                    String.format("%.2f", p.getAmount()),
                    p.getPaymentMethod(),
                    p.getReferenceNumber(),
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
                    p.getStudentName(),
                    String.format("%.2f", p.getAmount()),
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
