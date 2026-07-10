package school.enrollment.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import school.enrollment.controller.PaymentController;
import school.enrollment.controller.EnrollmentController;
import school.enrollment.controller.StudentController;
import school.enrollment.model.Enrollment;
import school.enrollment.model.Student;

public class PaymentView extends JPanel {
    private final PaymentController paymentController;
    private final EnrollmentController enrollmentController;
    private final StudentController studentController;
    private JComboBox<Student> cmbStudent;
    private JTable tblEnrollments;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtAmount;
    private JTextField txtReference;
    private JLabel lblTotalBalance, lblTotalPaid, lblSelectedBalance;
    private JLabel lblCount;

    public PaymentView() {
        paymentController = new PaymentController();
        enrollmentController = new EnrollmentController();
        studentController = new StudentController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        add(createStudentPanel(), BorderLayout.NORTH);
        add(createEnrollmentPanel(), BorderLayout.CENTER);
        add(createPaymentPanel(), BorderLayout.SOUTH);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Select Student"));

        cmbStudent = new JComboBox<>();
        cmbStudent.addActionListener(e -> loadStudentEnrollments());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            loadStudentCombo();
            loadStudentEnrollments();
        });

        panel.add(new JLabel("Student: "), BorderLayout.WEST);
        panel.add(cmbStudent, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.EAST);
        return panel;
    }

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Enrolled Courses - Check to select for payment"));

        String[] cols = {"Select", "Enroll ID", "Course Code", "Course", "Units", "Tuition", "Paid", "Balance"};
        tblEnrollments = new JTable(new DefaultTableModel(cols, 0) {
            public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            public boolean isCellEditable(int row, int col) { return false; }
        });
        tblEnrollments.getTableHeader().setReorderingAllowed(false);

        tblEnrollments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int row = tblEnrollments.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
                        model.setValueAt(!(Boolean) model.getValueAt(row, 0), row, 0);
                        updateSummary();
                    }
                }
            }
        });

        JPanel info = new JPanel(new GridLayout(1, 4, 10, 5));
        lblCount = new JLabel("Selected: 0 subjects");
        lblTotalBalance = new JLabel("Total Balance: P0.00");
        lblTotalPaid = new JLabel("Total Paid: P0.00");
        lblSelectedBalance = new JLabel("To Pay: P0.00");
        info.add(lblCount);
        info.add(lblTotalBalance);
        info.add(lblTotalPaid);
        info.add(lblSelectedBalance);

        panel.add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Process Payment"));

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtAmount = new JTextField(10);
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Bank Transfer", "Check"});
        txtReference = new JTextField(15);

        fields.add(new JLabel("Amount (P):"));
        fields.add(txtAmount);
        fields.add(new JLabel("Method:"));
        fields.add(cmbPaymentMethod);
        fields.add(new JLabel("Reference #:"));
        fields.add(txtReference);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnPaySelected = new JButton("Pay Selected Subjects");
        JButton btnPayAll = new JButton("Pay All Subjects");
        JButton btnClear = new JButton("Clear");

        btnPaySelected.addActionListener(e -> paySelected(false));
        btnPayAll.addActionListener(e -> paySelected(true));

        btnClear.addActionListener(e -> {
            txtAmount.setText("");
            cmbPaymentMethod.setSelectedIndex(0);
            txtReference.setText("");
            clearChecks();
        });

        buttons.add(btnPaySelected);
        buttons.add(btnPayAll);
        buttons.add(btnClear);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void paySelected(boolean all) {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        int[] rows;
        if (all) {
            rows = new int[model.getRowCount()];
            for (int i = 0; i < rows.length; i++) rows[i] = i;
        } else {
            List<Integer> checked = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                if ((Boolean) model.getValueAt(i, 0)) checked.add(i);
            }
            if (checked.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please check at least one subject to pay.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            rows = checked.stream().mapToInt(Integer::intValue).toArray();
        }

        String method = (String) cmbPaymentMethod.getSelectedItem();
        String ref = txtReference.getText().trim();
        String amountStr = txtAmount.getText().trim();
        double customAmount = 0;
        boolean useCustomAmount = false;
        if (!amountStr.isEmpty()) {
            try {
                customAmount = Double.parseDouble(amountStr);
                if (customAmount > 0) useCustomAmount = true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int paid = 0;

        for (int row : rows) {
            int enrollmentId = Integer.parseInt((String) model.getValueAt(row, 1));
            String balanceStr = ((String) model.getValueAt(row, 7)).replace(",", "");
            double balance = Double.parseDouble(balanceStr);
            if (balance <= 0) continue;
            double payAmount = useCustomAmount ? Math.min(customAmount, balance) : balance;
            paymentController.makePayment(enrollmentId, payAmount, method, ref);
            paid++;
        }

        if (paid > 0) {
            JOptionPane.showMessageDialog(this, paid + " payment(s) processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentEnrollments();
        } else {
            JOptionPane.showMessageDialog(this, "Selected subjects have no outstanding balance.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateSummary() {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        double totalBalance = 0, totalPaid = 0, selectedBalance = 0;
        int count = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String tuitionStr = ((String) model.getValueAt(i, 5)).replace(",", "");
            String paidStr = ((String) model.getValueAt(i, 6)).replace(",", "");
            String balanceStr = ((String) model.getValueAt(i, 7)).replace(",", "");
            double tuition = Double.parseDouble(tuitionStr);
            double paid = Double.parseDouble(paidStr);
            double balance = Double.parseDouble(balanceStr);
            totalBalance += balance;
            totalPaid += paid;

            if ((Boolean) model.getValueAt(i, 0)) {
                selectedBalance += balance;
                count++;
            }
        }

        lblCount.setText("Selected: " + count + " subject(s)");
        lblTotalBalance.setText("Total Balance: P" + String.format("%.2f", totalBalance));
        lblTotalPaid.setText("Total Paid: P" + String.format("%.2f", totalPaid));
        lblSelectedBalance.setText("To Pay: P" + String.format("%.2f", selectedBalance));
    }

    private void clearChecks() {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        for (int i = 0; i < model.getRowCount(); i++) model.setValueAt(false, i, 0);
        updateSummary();
    }

    public void loadStudentEnrollments() {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        model.setRowCount(0);

        Student s = (Student) cmbStudent.getSelectedItem();
        if (s == null) {
            updateSummary();
            return;
        }

        try {
            for (Enrollment e : enrollmentController.getEnrollmentsByStudent(s.getStudentId())) {
                double paid = enrollmentController.getTotalPaid(e.getEnrollmentId());
                double balance = e.getTotalTuition() - paid;
                model.addRow(new Object[]{
                    false,
                    String.valueOf(e.getEnrollmentId()),
                    e.getCourseCode(),
                    e.getCourseName(),
                    String.valueOf(e.getUnits()),
                    String.format("%.2f", e.getTotalTuition()),
                    String.format("%.2f", paid),
                    String.format("%.2f", Math.max(0, balance))
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateSummary();
    }

    public void loadStudentCombo() {
        cmbStudent.removeAllItems();
        try {
            for (Student s : studentController.getAllStudents()) cmbStudent.addItem(s);
        } catch (Exception ex) {
            // ignore
        }
    }

    public void loadPayments() {
        // no-op: table here shows enrollments, not payments
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadStudentCombo();
        loadStudentEnrollments();
    }
}
