package school.enrollment.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JTextField txtAmount, txtReference;
    private JLabel lblCount, lblTotalBalance, lblTotalPaid, lblSelectedBalance;

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
        cmbStudent.setFont(UIHelper.MAIN_FONT);
        cmbStudent.addActionListener(e -> { loadStudentEnrollments(); loadPaymentHistory(); });

        JButton btnRefresh = UIHelper.createButton("Refresh", UIHelper.ACCENT);
        btnRefresh.addActionListener(e -> { loadStudentCombo(); loadStudentEnrollments(); loadPaymentHistory(); });

        JLabel lbl = new JLabel("Student: ");
        UIHelper.styleLabel(lbl);
        panel.add(lbl, BorderLayout.WEST);
        panel.add(cmbStudent, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.EAST);
        return panel;
    }

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Enrolled Courses"));
        UIHelper.stylePanel(panel);

        String[] cols = {"Select", "Enroll ID", "Course Code", "Course", "Units", "Tuition", "Paid", "Balance"};
        tblEnrollments = new JTable(new DefaultTableModel(cols, 0) {
            public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblEnrollments);

        tblEnrollments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int row = tblEnrollments.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
                        m.setValueAt(!(Boolean) m.getValueAt(row, 0), row, 0);
                        updateSummary();
                    }
                }
            }
        });

        JPanel info = new JPanel(new GridLayout(1, 4, 15, 5));
        info.setBackground(UIHelper.PANEL_BG);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblCount = new JLabel("Selected: 0 subjects");
        lblTotalBalance = new JLabel("Total Balance: P0.00");
        lblTotalPaid = new JLabel("Total Paid: P0.00");
        lblSelectedBalance = new JLabel("To Pay: P0.00");
        for (JLabel l : new JLabel[]{lblCount, lblTotalBalance, lblTotalPaid, lblSelectedBalance}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
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

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        UIHelper.stylePanel(fields);
        txtAmount = new JTextField(10);
        UIHelper.styleField(txtAmount);
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Bank Transfer", "Check"});
        cmbPaymentMethod.setFont(UIHelper.MAIN_FONT);
        txtReference = new JTextField(15);
        UIHelper.styleField(txtReference);
        ((PlainDocument) txtReference.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                fb.replace(0, cur.length(), ns, null);
            }
        });

        JLabel l1 = new JLabel("Amount (P):");
        JLabel l2 = new JLabel("Method:");
        JLabel l3 = new JLabel("Reference #:");
        UIHelper.styleLabel(l1); UIHelper.styleLabel(l2); UIHelper.styleLabel(l3);
        fields.add(l1); fields.add(txtAmount);
        fields.add(l2); fields.add(cmbPaymentMethod);
        fields.add(l3); fields.add(txtReference);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnPaySelected = UIHelper.createButton("Pay Selected", UIHelper.SUCCESS);
        JButton btnPayAll = UIHelper.createButton("Pay All Subjects", UIHelper.ACCENT);
        JButton btnClear = UIHelper.createButton("Clear", new Color(149, 165, 166));

        btnPaySelected.addActionListener(e -> {
            paySelected(false);
            loadPaymentHistory();
        });
        btnPayAll.addActionListener(e -> {
            paySelected(true);
            loadPaymentHistory();
        });
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

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Payment History"));
        UIHelper.stylePanel(panel);

        String[] cols = {"Payment ID", "Course", "Amount", "Method", "Reference", "Date"};
        tblHistory = new JTable(new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblHistory);

        panel.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        return panel;
    }

    private void paySelected(boolean all) {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        int[] rows;
        if (all) {
            rows = new int[model.getRowCount()];
            for (int i = 0; i < rows.length; i++) { model.setValueAt(true, i, 0); rows[i] = i; }
        } else {
            List<Integer> checked = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++)
                if ((Boolean) model.getValueAt(i, 0)) checked.add(i);
            if (checked.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please check at least one subject.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            rows = checked.stream().mapToInt(Integer::intValue).toArray();
        }

        String method = (String) cmbPaymentMethod.getSelectedItem();
        String ref = txtReference.getText().trim();
        String amountStr = txtAmount.getText().trim();
        double customAmount = 0;
        boolean useCustom = false;
        if (!amountStr.isEmpty()) {
            try {
                customAmount = Double.parseDouble(amountStr);
                if (customAmount > 0) useCustom = true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int paid = 0;
        if (useCustom) {
            double totalBal = 0;
            for (int row : rows) {
                double bal = Double.parseDouble(((String) model.getValueAt(row, 7)).replace(",", ""));
                if (bal > 0) totalBal += bal;
            }
            double totalPay = Math.min(customAmount, totalBal);
            double allocated = 0;
            for (int i = 0; i < rows.length; i++) {
                int row = rows[i];
                int eid = Integer.parseInt((String) model.getValueAt(row, 1));
                double bal = Double.parseDouble(((String) model.getValueAt(row, 7)).replace(",", ""));
                if (bal <= 0) continue;
                double payAmt;
                if (i == rows.length - 1) {
                    payAmt = Math.round((totalPay - allocated) * 100.0) / 100.0;
                } else {
                    payAmt = Math.round((bal / totalBal * totalPay) * 100.0) / 100.0;
                }
                allocated += payAmt;
                if (payAmt > 0) { paymentController.makePayment(eid, payAmt, method, ref); paid++; }
            }
        } else {
            for (int row : rows) {
                int eid = Integer.parseInt((String) model.getValueAt(row, 1));
                double bal = Double.parseDouble(((String) model.getValueAt(row, 7)).replace(",", ""));
                if (bal <= 0) continue;
                paymentController.makePayment(eid, bal, method, ref);
                paid++;
            }
        }

        if (paid > 0) {
            JOptionPane.showMessageDialog(this, paid + " payment(s) processed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentEnrollments();
        } else {
            JOptionPane.showMessageDialog(this, "No outstanding balance.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateSummary() {
        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
        double totalBal = 0, totalPaid = 0, selBal = 0;
        int count = 0;
        for (int i = 0; i < m.getRowCount(); i++) {
            double paid = Double.parseDouble(((String) m.getValueAt(i, 6)).replace(",", ""));
            double bal = Double.parseDouble(((String) m.getValueAt(i, 7)).replace(",", ""));
            totalBal += bal; totalPaid += paid;
            if ((Boolean) m.getValueAt(i, 0)) { selBal += bal; count++; }
        }
        lblCount.setText("Selected: " + count + " subject(s)");
        lblTotalBalance.setText("Total Balance: P" + String.format("%.2f", totalBal));
        lblTotalPaid.setText("Total Paid: P" + String.format("%.2f", totalPaid));
        lblSelectedBalance.setText("To Pay: P" + String.format("%.2f", selBal));
    }

    private void clearChecks() {
        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
        for (int i = 0; i < m.getRowCount(); i++) m.setValueAt(false, i, 0);
        updateSummary();
    }

    public void loadStudentEnrollments() {
        DefaultTableModel m = (DefaultTableModel) tblEnrollments.getModel();
        m.setRowCount(0);
        Student s = (Student) cmbStudent.getSelectedItem();
        if (s == null) { updateSummary(); return; }
        try {
            for (Enrollment e : enrollmentController.getEnrollmentsByStudent(s.getStudentId())) {
                double paid = enrollmentController.getTotalPaid(e.getEnrollmentId());
                double bal = e.getTotalTuition() - paid;
                m.addRow(new Object[]{false, String.valueOf(e.getEnrollmentId()), e.getCourseCode(),
                    e.getCourseName(), String.valueOf(e.getUnits()),
                    String.format("%.2f", e.getTotalTuition()),
                    String.format("%.2f", paid), String.format("%.2f", Math.max(0, bal))});
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
        if (s == null) return;
        try {
            for (Payment p : paymentController.getAllPaymentsByStudent(s.getStudentId()))
                m.addRow(new Object[]{p.getPaymentId(), p.getCourseName(),
                    String.format("%.2f", p.getAmount()), p.getPaymentMethod(),
                    p.getReferenceNumber(), p.getPaymentDate()});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadStudentCombo() {
        cmbStudent.removeAllItems();
        try { for (Student s : studentController.getAllStudents()) cmbStudent.addItem(s); }
        catch (Exception ex) { /* ignore */ }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadStudentCombo();
        loadStudentEnrollments();
        loadPaymentHistory();
    }
}
