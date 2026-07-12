package school.enrollment.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import school.enrollment.controller.PaymentController;
import school.enrollment.controller.EnrollmentController;
import school.enrollment.controller.StudentController;
import school.enrollment.model.Enrollment;
import school.enrollment.model.Payment;
import school.enrollment.model.Student;

public class PaymentView extends JPanel {
    private final PaymentController paymentController;
    private final EnrollmentController enrollmentController;
    private final StudentController studentController;
    private JComboBox<Student> cmbStudent;
    private JTable tblEnrollments, tblHistory;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtAmount, txtReference, txtHistorySearch;
    private JLabel lblTotalBalance, lblTotalPaid, lblSelectedBalance;
    private List<Enrollment> currentEnrollments = Collections.emptyList();

    public PaymentView() {
        paymentController = new PaymentController();
        enrollmentController = new EnrollmentController();
        studentController = new StudentController();
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        add(createStudentPanel(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.55);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setTopComponent(createEnrollmentPanel());
        split.setBottomComponent(createBottomPanel());
        add(split, BorderLayout.CENTER);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 5));
        panel.setBorder(UIHelper.createBorder("Select Student"));
        UIHelper.stylePanel(panel);
        cmbStudent = new JComboBox<>();
        UIHelper.styleComboBox(cmbStudent);
        UIHelper.setComboPlaceholder(cmbStudent, "Select student...");
        cmbStudent.addActionListener(e -> { loadStudentEnrollments(); loadPaymentHistory(); });

        JButton btnClear = UIHelper.createGhostButton("Clear");
        btnClear.addActionListener(e -> {
            if (cmbStudent.getItemCount() > 0) {
                cmbStudent.setSelectedIndex(0);
            }
            loadStudentEnrollments();
            loadPaymentHistory();
        });

        JPanel studentRow = new JPanel(new BorderLayout(8, 0));
        studentRow.setOpaque(false);
        studentRow.add(cmbStudent, BorderLayout.CENTER);
        studentRow.add(btnClear, BorderLayout.EAST);

        panel.add(UIHelper.createLabeledField("Student", studentRow), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Tuition Balance Summaries"));
        UIHelper.stylePanel(panel);

        String[] cols = {"Student ID", "Student Name", "Total Tuition", "Payment Made", "Balance Left"};
        tblEnrollments = new JTable(new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblEnrollments);
        tblEnrollments.setRowSelectionAllowed(true);
        tblEnrollments.setColumnSelectionAllowed(false);
        tblEnrollments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEnrollments.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
                int[] selectedRows = tblEnrollments.getSelectedRows();
                for (int viewRow : selectedRows) {
                    int modelRow = tblEnrollments.convertRowIndexToModel(viewRow);
                    double bal = Double.parseDouble(((String) model.getValueAt(modelRow, 4)).replace(",", ""));
                    if (bal <= 0) {
                        tblEnrollments.removeRowSelectionInterval(viewRow, viewRow);
                    }
                }
                updateSummary();
            }
        });
        tblEnrollments.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int viewRow = tblEnrollments.rowAtPoint(e.getPoint());
                if (viewRow >= 0) {
                    int modelRow = tblEnrollments.convertRowIndexToModel(viewRow);
                    DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
                    double bal = Double.parseDouble(((String) model.getValueAt(modelRow, 4)).replace(",", ""));
                    if (bal <= 0) {
                        tblEnrollments.clearSelection();
                        e.consume();
                    }
                }
            }
        });

        JPanel info = new JPanel(new GridLayout(1, 3, 15, 5));
        info.setBackground(UIHelper.PANEL_BG);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblTotalBalance = new JLabel("Total Balance: P0.00");
        lblTotalPaid = new JLabel("Total Paid: P0.00");
        lblSelectedBalance = new JLabel("To Pay: P0.00");
        for (JLabel l : new JLabel[]{lblTotalBalance, lblTotalPaid, lblSelectedBalance}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 16));
            l.setForeground(UIHelper.LABEL_FG);
            info.add(l);
        }

        panel.add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        UIHelper.stylePanel(p);
        p.add(createPaymentFormPanel(), BorderLayout.NORTH);
        p.add(createHistoryPanel(), BorderLayout.CENTER);
        return p;
    }

    private JPanel createPaymentFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Process Payment"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridLayout(0, 2, 18, 12));
        UIHelper.stylePanel(fields);
        txtAmount = new JTextField(10);
        UIHelper.styleField(txtAmount);
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Bank Transfer", "Check"});
        cmbPaymentMethod.setFont(UIHelper.MAIN_FONT);
        txtReference = new JTextField(15);
        UIHelper.styleField(txtReference);

        fields.add(UIHelper.createLabeledField("Amount (P)*", txtAmount));
        fields.add(UIHelper.createLabeledField("Method", cmbPaymentMethod));
        fields.add(UIHelper.createLabeledField("Reference #*", txtReference));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnPay = UIHelper.createButton("Pay Balance", UIHelper.SUCCESS);
        JButton btnClear = UIHelper.createGhostButton("Clear");

        btnPay.addActionListener(e -> {
            paySelected();
            loadPaymentHistory();
        });
        btnClear.addActionListener(e -> {
            txtAmount.setText("");
            cmbPaymentMethod.setSelectedIndex(0);
            txtReference.setText("");
            tblEnrollments.clearSelection();
            updateSummary();
        });

        buttons.add(btnPay);
        buttons.add(btnClear);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Payment History"));
        UIHelper.stylePanel(panel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        UIHelper.stylePanel(searchPanel);
        txtHistorySearch = new JTextField();
        UIHelper.styleField(txtHistorySearch);
        UIHelper.setPlaceholder(txtHistorySearch, "Search payments...");
        searchPanel.add(txtHistorySearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Show All");
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        String[] cols = {"Name", "Payment", "Method", "Reference", "Date"};
        tblHistory = new JTable(new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblHistory);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tblHistory.getModel());
        tblHistory.setRowSorter(sorter);
        JLabel emptyHistory = UIHelper.createEmptyStateLabel("No payment history found.");

        txtHistorySearch.addActionListener(e -> {
            searchPaymentHistory();
            UIHelper.setEmptyStateVisible(tblHistory, emptyHistory);
        });
        btnSearch.addActionListener(e -> {
            searchPaymentHistory();
            UIHelper.setEmptyStateVisible(tblHistory, emptyHistory);
        });
        btnRefresh.addActionListener(e -> {
            txtHistorySearch.setText("");
            loadPaymentHistory();
            UIHelper.setEmptyStateVisible(tblHistory, emptyHistory);
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(UIHelper.createTableWithOverlay(tblHistory, emptyHistory), BorderLayout.CENTER);
        UIHelper.setEmptyStateVisible(tblHistory, emptyHistory);
        return panel;
    }

    private void searchPaymentHistory() {
        DefaultRowSorter<?, ?> sorter = (DefaultRowSorter<?, ?>) tblHistory.getRowSorter();
        String kw = UIHelper.getCleanText(txtHistorySearch);
        if (kw.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(kw)));
        }
    }

    private void paySelected() {
        int[] selectedRows = tblEnrollments.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to pay.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = selectedRows[0];
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        String studentId = (String) model.getValueAt(row, 0);
        double bal = Double.parseDouble(((String) model.getValueAt(row, 4)).replace(",", ""));
        if (bal <= 0) {
            JOptionPane.showMessageDialog(this, "This student has no balance left and is already fully paid.", "Already Paid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String method = (String) cmbPaymentMethod.getSelectedItem();
        String ref = UIHelper.getCleanText(txtReference);
        String amountStr = UIHelper.getCleanText(txtAmount);
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ref.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double customAmount;
        try {
            customAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (customAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (customAmount > bal) {
            customAmount = bal;
        }

        // Get enrollments for the student to pay them
        List<Enrollment> studentEnrs = enrollmentController.getEnrollmentsByStudent(studentId);
        if (studentEnrs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active enrollments for this student.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double remainingAmount = customAmount;
        int payments = 0;
        for (int i = 0; i < studentEnrs.size(); i++) {
            Enrollment enrollment = studentEnrs.get(i);
            double eBal = enrollment.getTotalTuition() - enrollmentController.getTotalPaid(enrollment.getEnrollmentId());
            if (eBal <= 0) continue;
            double payAmt = i == studentEnrs.size() - 1 ? Math.round(remainingAmount * 100.0) / 100.0 : Math.min(eBal, Math.round((eBal / bal * customAmount) * 100.0) / 100.0);
            remainingAmount -= payAmt;
            paymentController.makePayment(enrollment.getEnrollmentId(), payAmt, method, ref);
            payments++;
        }

        if (payments > 0) {
            JOptionPane.showMessageDialog(this, "Processed " + payments + " payment(s) successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentEnrollments();
            txtAmount.setText("");
            txtReference.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "No applicable balance found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateSummary() {
        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
        double totalBal = 0;
        double totalPaid = 0;
        double toPay = 0;

        int selectedRow = tblEnrollments.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tblEnrollments.convertRowIndexToModel(selectedRow);
            totalPaid = Double.parseDouble(((String) m.getValueAt(modelRow, 3)).replace(",", ""));
            totalBal = Double.parseDouble(((String) m.getValueAt(modelRow, 4)).replace(",", ""));
            toPay = totalBal;
        }

        lblTotalBalance.setText("Total Balance: P" + String.format("%.2f", totalBal));
        lblTotalPaid.setText("Total Paid: P" + String.format("%.2f", totalPaid));
        lblSelectedBalance.setText("To Pay: P" + String.format("%.2f", toPay));
    }

    public void loadStudentEnrollments() {
        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
        m.setRowCount(0);
        tblEnrollments.clearSelection();
        Student selectedStudent = (Student) cmbStudent.getSelectedItem();
        try {
            List<Enrollment> enrollments = enrollmentController.getAllEnrollments();
            java.util.Map<String, List<Enrollment>> studentGroups = new java.util.LinkedHashMap<>();
            for (Enrollment e : enrollments) {
                studentGroups.computeIfAbsent(e.getStudentId(), k -> new ArrayList<>()).add(e);
            }

            if (selectedStudent != null && selectedStudent.getStudentId() != null && !selectedStudent.getStudentId().isEmpty()) {
                List<Enrollment> studentEnrs = studentGroups.get(selectedStudent.getStudentId());
                if (studentEnrs != null && !studentEnrs.isEmpty()) {
                    double totalTuition = 0;
                    double totalPaid = 0;
                    for (Enrollment e : studentEnrs) {
                        totalTuition += e.getTotalTuition();
                        totalPaid += enrollmentController.getTotalPaid(e.getEnrollmentId());
                    }
                    double balanceLeft = Math.max(0, totalTuition - totalPaid);
                    m.addRow(new Object[]{
                        selectedStudent.getStudentId(),
                        selectedStudent.getFirstName() + " " + selectedStudent.getLastName(),
                        String.format("%.2f", totalTuition),
                        String.format("%.2f", totalPaid),
                        String.format("%.2f", balanceLeft)
                    });
                }
            } else {
                for (java.util.Map.Entry<String, List<Enrollment>> entry : studentGroups.entrySet()) {
                    String studentId = entry.getKey();
                    List<Enrollment> studentEnrs = entry.getValue();
                    String studentName = studentEnrs.get(0).getStudentName();

                    double totalTuition = 0;
                    double totalPaid = 0;
                    for (Enrollment e : studentEnrs) {
                        totalTuition += e.getTotalTuition();
                        totalPaid += enrollmentController.getTotalPaid(e.getEnrollmentId());
                    }
                    double balanceLeft = Math.max(0, totalTuition - totalPaid);
                    m.addRow(new Object[]{
                        studentId,
                        studentName,
                        String.format("%.2f", totalTuition),
                        String.format("%.2f", totalPaid),
                        String.format("%.2f", balanceLeft)
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateSummary();
    }

    public void loadPaymentHistory() {
        DefaultTableModel m = (DefaultTableModel) tblHistory.getModel();
        m.setRowCount(0);
        Student s = (Student) cmbStudent.getSelectedItem();
        try {
            if (s == null || s.getStudentId() == null || s.getStudentId().isEmpty()) {
                paymentController.loadPayments(tblHistory);
                return;
            }
            List<Payment> rawPayments = paymentController.getAllPaymentsByStudent(s.getStudentId());
            List<Payment> aggregated = paymentController.aggregatePayments(rawPayments);
            for (Payment p : aggregated) {
                m.addRow(new Object[]{
                    p.getStudentName(),
                    String.format("%.2f", p.getAmount()),
                    p.getPaymentMethod(),
                    p.getReferenceNumber(),
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadStudentCombo() {
        cmbStudent.removeAllItems();
        cmbStudent.addItem(new Student("", "", "", "", "", "") {
            @Override
            public String toString() {
                return "";
            }
        });
        try {
            for (Student s : studentController.getAllStudents()) cmbStudent.addItem(s);
        } catch (Exception ex) { /* ignore */ }
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadStudentCombo();
        loadStudentEnrollments();
        loadPaymentHistory();
    }
}
