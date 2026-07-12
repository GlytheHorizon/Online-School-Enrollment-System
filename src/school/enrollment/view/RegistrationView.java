package school.enrollment.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import school.enrollment.controller.EnrollmentController;
import school.enrollment.controller.StudentController;
import school.enrollment.model.Enrollment;
import school.enrollment.model.Student;

public class RegistrationView extends JPanel {
    private final StudentController controller;
    private final EnrollmentController enrollmentController;
    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress;
    private JTable tblStudents;
    private JTextField txtSearch;
    private String selectedStudentId;

    public RegistrationView() {
        controller = new StudentController();
        enrollmentController = new EnrollmentController();
        selectedStudentId = null;
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.38);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setTopComponent(createFormPanel());
        split.setBottomComponent(createTablePanel());
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIHelper.createBorder("Student Registration Form"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridLayout(0, 2, 18, 12));
        UIHelper.stylePanel(fields);

        txtStudentId = new JTextField(10);
        txtFirstName = new JTextField(20);
        txtLastName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);
        txtAddress = new JTextField(20);
        for (JTextField f : new JTextField[]{txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress})
            UIHelper.styleField(f);

        ((PlainDocument) txtPhone.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 11) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 11) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                fb.replace(0, cur.length(), ns, null);
            }
        });

        ((PlainDocument) txtStudentId.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), null);
            }
        });

        UIHelper.setPlaceholder(txtStudentId, "2024-0001");
        UIHelper.setPlaceholder(txtFirstName, "e.g., Juan");
        UIHelper.setPlaceholder(txtLastName, "e.g., Dela Cruz");
        UIHelper.setPlaceholder(txtEmail, "email@example.com");
        UIHelper.setPlaceholder(txtPhone, "09XXXXXXXXX");
        UIHelper.setPlaceholder(txtAddress, "Street, City, Province");

        fields.add(UIHelper.createLabeledField("Student ID*", txtStudentId));
        fields.add(UIHelper.createLabeledField("First Name*", txtFirstName));
        fields.add(UIHelper.createLabeledField("Last Name*", txtLastName));
        fields.add(UIHelper.createLabeledField("Email*", txtEmail));
        fields.add(UIHelper.createLabeledField("Phone", txtPhone));
        fields.add(UIHelper.createLabeledField("Address", txtAddress));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnSave = UIHelper.createButton("Register", UIHelper.SUCCESS);
        JButton btnClear = UIHelper.createGhostButton("Clear");

        btnSave.addActionListener(e -> {
            controller.registerStudent(
                UIHelper.getCleanText(txtStudentId),
                UIHelper.getCleanText(txtFirstName),
                UIHelper.getCleanText(txtLastName),
                UIHelper.getCleanText(txtEmail),
                UIHelper.getCleanText(txtPhone),
                UIHelper.getCleanText(txtAddress));
            clearForm(); controller.loadStudents(tblStudents);
        });
        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnSave); buttons.add(btnClear);
        
        JPanel fieldsWrapper = new JPanel(new BorderLayout());
        UIHelper.stylePanel(fieldsWrapper);
        fieldsWrapper.add(fields, BorderLayout.NORTH);
        
        panel.add(fieldsWrapper, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Registered Students"));
        UIHelper.stylePanel(panel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        UIHelper.stylePanel(searchPanel);
        txtSearch = new JTextField();
        UIHelper.styleField(txtSearch);
        UIHelper.setPlaceholder(txtSearch, "Search students...");
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Refresh");
        tblStudents = new JTable(new DefaultTableModel(new Object[]{"Student ID", "First Name", "Last Name", "Email", "Phone", "Address"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        tblStudents.setRowHeight(40);
        UIHelper.styleTable(tblStudents);
        JLabel emptyStudents = UIHelper.createEmptyStateLabel("No students found.");
        txtSearch.addActionListener(e -> {
            controller.searchStudents(tblStudents, UIHelper.getCleanText(txtSearch));
            UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);
        });
        btnSearch.addActionListener(e -> {
            controller.searchStudents(tblStudents, UIHelper.getCleanText(txtSearch));
            UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);
        });
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); controller.loadStudents(tblStudents); UIHelper.setEmptyStateVisible(tblStudents, emptyStudents); });
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);
        tblStudents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblStudents.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    openStudentDetails(row);
                }
            }
        });
        controller.loadStudents(tblStudents);
        UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(UIHelper.createTableWithOverlay(tblStudents, emptyStudents), BorderLayout.CENTER);
        return panel;
    }

    private void clearForm() {
        txtStudentId.setText("2024-0001");
        txtFirstName.setText("e.g., Juan");
        txtLastName.setText("e.g., Dela Cruz");
        txtEmail.setText("email@example.com");
        txtPhone.setText("09XXXXXXXXX");
        txtAddress.setText("Street, City, Province");
        for (JTextField f : new JTextField[]{txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress})
            f.setForeground(Color.GRAY);
        selectedStudentId = null;
        tblStudents.clearSelection();
    }

    private void openStudentDetails(int row) {
        if (row < 0) return;
        String studentId = (String) tblStudents.getValueAt(row, 0);
        Student student = controller.getStudent(studentId);
        if (student != null) {
            new StudentDetailsDialog(student).setVisible(true);
            controller.loadStudents(tblStudents);
        }
    }

    private class StudentDetailsDialog extends JDialog {
        private final JTextField txtStudentId;
        private final JTextField txtFirstName;
        private final JTextField txtLastName;
        private final JTextField txtEmail;
        private final JTextField txtPhone;
        private final JTextField txtAddress;
        private final JButton btnEdit;
        private final JButton btnDelete;
        private final JButton btnSave;
        private final JButton btnCancel;

        private final Student student;

        public StudentDetailsDialog(Student student) {
            super(SwingUtilities.getWindowAncestor(RegistrationView.this), "Student Details", ModalityType.APPLICATION_MODAL);
            this.student = student;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setPreferredSize(new Dimension(1040, 720));
            setMinimumSize(new Dimension(960, 660));
            setResizable(true);

            JPanel dialogRoot = new JPanel(new GridBagLayout());
            dialogRoot.setBackground(new Color(235, 239, 246));
            dialogRoot.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel card = new JPanel(new BorderLayout(18, 18));
            card.setPreferredSize(new Dimension(860, 620));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            JLabel title = new JLabel("Student Details: " + student.getStudentId());
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            title.setForeground(new Color(15, 23, 42));
            header.add(title, BorderLayout.WEST);
            card.add(header, BorderLayout.NORTH);

            JPanel detailGrid = new JPanel(new GridLayout(0, 2, 10, 8));
            detailGrid.setOpaque(false);

            txtStudentId = new JTextField(student.getStudentId());
            txtFirstName = new JTextField(student.getFirstName());
            txtLastName = new JTextField(student.getLastName());
            txtEmail = new JTextField(student.getEmail());
            txtPhone = new JTextField(student.getPhone());
            txtAddress = new JTextField(student.getAddress());
            UIHelper.styleField(txtStudentId);
            UIHelper.styleField(txtFirstName);
            UIHelper.styleField(txtLastName);
            UIHelper.styleField(txtEmail);
            UIHelper.styleField(txtPhone);
            UIHelper.styleField(txtAddress);

            detailGrid.add(UIHelper.createLabeledField("Student ID", txtStudentId));
            detailGrid.add(UIHelper.createLabeledField("Email", txtEmail));
            detailGrid.add(UIHelper.createLabeledField("First Name", txtFirstName));
            detailGrid.add(UIHelper.createLabeledField("Phone", txtPhone));
            detailGrid.add(UIHelper.createLabeledField("Last Name", txtLastName));
            detailGrid.add(UIHelper.createLabeledField("Address", txtAddress));

            JPanel coursesPanel = new JPanel(new BorderLayout(8, 8));
            coursesPanel.setOpaque(false);
            coursesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                "Enrolled Courses", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)));

            JTable courseTable = new JTable(new DefaultTableModel(new Object[]{"Course Code", "Course", "Units", "Tuition", "Paid", "Balance"}, 0) {
                public boolean isCellEditable(int row, int col) { return false; }
            });
            UIHelper.styleTable(courseTable);
            JScrollPane courseScroll = new JScrollPane(courseTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            UIHelper.styleScrollPane(courseScroll);
            courseScroll.setPreferredSize(new Dimension(200, 150));
            coursesPanel.add(courseScroll, BorderLayout.CENTER);

            List<Enrollment> enrollments = enrollmentController.getEnrollmentsByStudent(student.getStudentId());
            for (Enrollment e : enrollments) {
                double paid = enrollmentController.getTotalPaid(e.getEnrollmentId());
                double bal = Math.max(0, e.getTotalTuition() - paid);
                ((DefaultTableModel) courseTable.getModel()).addRow(new Object[]{e.getCourseCode(), e.getCourseName(), e.getUnits(), String.format("%.2f", e.getTotalTuition()), String.format("%.2f", paid), String.format("%.2f", bal)});
            }

            btnDelete = UIHelper.createButton("Delete", new Color(192, 57, 43));
            btnEdit = UIHelper.createSecondaryButton("Edit");
            btnSave = UIHelper.createButton("Save", new Color(22, 163, 74));
            btnCancel = UIHelper.createSecondaryButton("Cancel");

            btnSave.setVisible(false);
            btnCancel.setVisible(false);

            btnEdit.addActionListener(e -> setEditMode(true));
            btnDelete.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Delete student " + student.getStudentId() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    controller.deleteStudent(student.getStudentId());
                    dispose();
                }
            });
            btnSave.addActionListener(e -> {
                controller.updateStudent(
                    student.getStudentId(),
                    UIHelper.getCleanText(txtFirstName),
                    UIHelper.getCleanText(txtLastName),
                    UIHelper.getCleanText(txtEmail),
                    UIHelper.getCleanText(txtPhone),
                    UIHelper.getCleanText(txtAddress));
                setEditMode(false);
                dispose();
            });
            btnCancel.addActionListener(e -> {
                setEditMode(false);
                loadDetails();
            });

            JPanel editBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
            editBar.setOpaque(false);
            editBar.add(btnEdit);
            editBar.add(btnSave);
            editBar.add(btnCancel);

            JPanel detailWrapper = new JPanel(new GridBagLayout());
            detailWrapper.setOpaque(false);
            GridBagConstraints dwGbc = new GridBagConstraints();
            dwGbc.gridx = 0; dwGbc.gridy = 0;
            dwGbc.weightx = 1.0; dwGbc.weighty = 0.0;
            dwGbc.fill = GridBagConstraints.HORIZONTAL;
            detailWrapper.add(detailGrid, dwGbc);

            dwGbc.gridy = 1;
            dwGbc.insets = new Insets(10, 0, 0, 0);
            detailWrapper.add(editBar, dwGbc);

            dwGbc.gridy = 2;
            dwGbc.weighty = 1.0;
            dwGbc.fill = GridBagConstraints.BOTH;
            detailWrapper.add(Box.createGlue(), dwGbc);

            card.add(detailWrapper, BorderLayout.CENTER);

            JPanel deleteBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            deleteBar.setOpaque(false);
            deleteBar.add(btnDelete);

            JPanel bottomWrapper = new JPanel(new BorderLayout(8, 8));
            bottomWrapper.setOpaque(false);
            bottomWrapper.add(coursesPanel, BorderLayout.CENTER);
            bottomWrapper.add(deleteBar, BorderLayout.SOUTH);
            card.add(bottomWrapper, BorderLayout.SOUTH);

            setEditMode(false);
            dialogRoot.add(card);
            setContentPane(dialogRoot);
            pack();
            setLocationRelativeTo(getOwner());
        }

        private void loadDetails() {
            txtStudentId.setText(student.getStudentId());
            txtFirstName.setText(student.getFirstName());
            txtLastName.setText(student.getLastName());
            txtEmail.setText(student.getEmail());
            txtPhone.setText(student.getPhone());
            txtAddress.setText(student.getAddress());
        }

        private void styleFieldMode(JTextField field, boolean editable) {
            field.setEditable(editable);
            field.setFocusable(editable);
            if (editable) {
                UIHelper.styleField(field);
                field.setBackground(Color.WHITE);
                field.setOpaque(true);
                field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            } else {
                field.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                field.setBackground(Color.WHITE);
                field.setOpaque(false);
                field.setCursor(Cursor.getDefaultCursor());
            }
        }

        private void setEditMode(boolean editable) {
            styleFieldMode(txtStudentId, false);
            styleFieldMode(txtFirstName, editable);
            styleFieldMode(txtLastName, editable);
            styleFieldMode(txtEmail, editable);
            styleFieldMode(txtPhone, editable);
            styleFieldMode(txtAddress, editable);
            btnEdit.setVisible(!editable);
            btnDelete.setVisible(!editable);
            btnSave.setVisible(editable);
            btnCancel.setVisible(editable);
        }
    }
}
