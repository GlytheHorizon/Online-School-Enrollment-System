package school.enrollment.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    // Form fields
    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTextField txtBirthDate, txtBirthPlace, txtAddress;
    private JComboBox<String> cmbCivilStatus, cmbSex;

    private JTable tblStudents;
    private JTextField txtSearch;
    private boolean showArchived = false;

    private static final String[] CIVIL_STATUSES = {"", "Single", "Married", "Widowed", "Separated", "Divorced"};
    private static final String[] SEXES          = {"", "Male", "Female"};
    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public RegistrationView() {
        controller = new StudentController();
        enrollmentController = new EnrollmentController();
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.45);
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

        txtStudentId  = new JTextField(10);
        txtFirstName  = new JTextField(20);
        txtLastName   = new JTextField(20);
        txtEmail      = new JTextField(20);
        txtPhone      = new JTextField(20);
        txtBirthDate  = new JTextField(10);
        txtBirthPlace = new JTextField(20);
        txtAddress    = new JTextField(20);
        cmbCivilStatus = new JComboBox<>(CIVIL_STATUSES);
        cmbSex         = new JComboBox<>(SEXES);

        for (JTextField f : new JTextField[]{txtStudentId, txtFirstName, txtLastName,
                                             txtEmail, txtPhone, txtBirthDate, txtBirthPlace, txtAddress})
            UIHelper.styleField(f);
        UIHelper.styleComboBox(cmbCivilStatus);
        UIHelper.styleComboBox(cmbSex);

        // Phone: digits only, max 11
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

        // Student ID: XXXX-XXXX format, digits only
        ((PlainDocument) txtStudentId.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            private String fmt(String d) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < d.length(); i++) { if (i == 4) sb.append('-'); sb.append(d.charAt(i)); }
                return sb.toString();
            }
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                fb.replace(0, cur.length(), fmt(ns), a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                fb.replace(0, cur.length(), fmt(ns), a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                fb.replace(0, cur.length(), fmt(ns), null);
            }
        });

        UIHelper.setPlaceholder(txtStudentId,  "2024-0001");
        UIHelper.setPlaceholder(txtFirstName,  "e.g., Juan");
        UIHelper.setPlaceholder(txtLastName,   "e.g., Dela Cruz");
        UIHelper.setPlaceholder(txtEmail,      "email@example.com");
        UIHelper.setPlaceholder(txtPhone,      "09XXXXXXXXX");
        UIHelper.setPlaceholder(txtBirthDate,  "MM/DD/YYYY");
        UIHelper.setPlaceholder(txtBirthPlace, "e.g., Manila");
        UIHelper.setPlaceholder(txtAddress,    "Street, City, Province");

        fields.add(UIHelper.createLabeledField("Student ID*",   txtStudentId));
        fields.add(UIHelper.createLabeledField("First Name*",   txtFirstName));
        fields.add(UIHelper.createLabeledField("Last Name*",    txtLastName));
        fields.add(UIHelper.createLabeledField("Email*",        txtEmail));
        fields.add(UIHelper.createLabeledField("Phone",         txtPhone));
        fields.add(UIHelper.createLabeledField("Birth Date*",   txtBirthDate));
        fields.add(UIHelper.createLabeledField("Birth Place",   txtBirthPlace));
        fields.add(UIHelper.createLabeledField("Civil Status*", cmbCivilStatus));
        fields.add(UIHelper.createLabeledField("Sex*",          cmbSex));
        fields.add(UIHelper.createLabeledField("Address",       txtAddress));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnSave  = UIHelper.createButton("Register", UIHelper.SUCCESS);
        JButton btnClear = UIHelper.createGhostButton("Clear");

        btnSave.addActionListener(e -> {
            LocalDate bd = parseBirthDate(UIHelper.getCleanText(txtBirthDate));
            String rawBD = UIHelper.getCleanText(txtBirthDate);
            if (!rawBD.isEmpty() && bd == null) return;
            String cs  = (String) cmbCivilStatus.getSelectedItem();
            String sex = (String) cmbSex.getSelectedItem();
            controller.registerStudent(
                UIHelper.getCleanText(txtStudentId),
                UIHelper.getCleanText(txtFirstName),
                UIHelper.getCleanText(txtLastName),
                UIHelper.getCleanText(txtEmail),
                UIHelper.getCleanText(txtPhone),
                bd,
                UIHelper.getCleanText(txtBirthPlace),
                cs  == null ? "" : cs,
                sex == null ? "" : sex,
                UIHelper.getCleanText(txtAddress));
            clearForm(); loadStudentTable();
        });
        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnSave);
        buttons.add(btnClear);

        JPanel fieldsWrapper = new JPanel(new BorderLayout());
        UIHelper.stylePanel(fieldsWrapper);
        fieldsWrapper.add(fields, BorderLayout.NORTH);

        panel.add(fieldsWrapper, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    LocalDate parseBirthDate(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Birth date must be in MM/DD/YYYY format (e.g., 01/15/2000).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
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

        JLabel emptyStudents = UIHelper.createEmptyStateLabel("No students found.");

        Color archiveColor = new Color(139, 92, 246);
        Color activeColor = new Color(22, 163, 74);
        JLabel lblListType = new JLabel("Active List");
        lblListType.setFont(UIHelper.MAIN_FONT.deriveFont(Font.BOLD, 14f));
        lblListType.setForeground(activeColor);
        lblListType.setHorizontalAlignment(SwingConstants.CENTER);
        JButton btnArchive = UIHelper.createOutlineButton("Show Archived", archiveColor);
        btnArchive.addActionListener(e -> {
            showArchived = !showArchived;
            btnArchive.setText(showArchived ? "Show Active" : "Show Archived");
            Color c = showArchived ? activeColor : archiveColor;
            btnArchive.setForeground(c);
            btnArchive.setBorder(BorderFactory.createCompoundBorder(
                UIHelper.createRoundedBorder(14, c),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            lblListType.setText(showArchived ? "Archive List" : "Active List");
            lblListType.setForeground(showArchived ? archiveColor : activeColor);
            emptyStudents.setText(showArchived ? "No archived students." : "No students found.");
            loadStudentTable();
            UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);
        });
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Refresh");

        tblStudents = new JTable(new DefaultTableModel(
            new Object[]{"Student ID", "First Name", "Last Name", "Email", "Phone", "Address"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        tblStudents.setRowHeight(40);
        UIHelper.styleTable(tblStudents);
        txtSearch.addActionListener(e -> {
            searchStudentTable();
            UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);
        });
        btnSearch.addActionListener(e -> {
            searchStudentTable();
            UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);
        });
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadStudentTable(); UIHelper.setEmptyStateVisible(tblStudents, emptyStudents); });

        // Row 1: search field + Search, Refresh, Show Archived buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        UIHelper.stylePanel(btnPanel);
        btnPanel.add(btnSearch);
        btnPanel.add(btnArchive);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        // Row 2: list type label on the left
        JPanel archiveRow = new JPanel(new BorderLayout());
        archiveRow.setOpaque(false);
        archiveRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        lblListType.setHorizontalAlignment(SwingConstants.CENTER);
        archiveRow.add(lblListType, BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(searchPanel, BorderLayout.NORTH);
        topSection.add(archiveRow, BorderLayout.SOUTH);

        tblStudents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblStudents.rowAtPoint(e.getPoint());
                if (row >= 0) openStudentDetails(row);
            }
        });
        loadStudentTable();
        UIHelper.setEmptyStateVisible(tblStudents, emptyStudents);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(UIHelper.createTableWithOverlay(tblStudents, emptyStudents), BorderLayout.CENTER);
        return panel;
    }

    private void loadStudentTable() {
        if (showArchived) {
            controller.loadInactiveStudents(tblStudents);
        } else {
            controller.loadStudents(tblStudents);
        }
    }

    private void searchStudentTable() {
        String kw = UIHelper.getCleanText(txtSearch);
        if (showArchived) {
            controller.searchInactiveStudents(tblStudents, kw);
        } else {
            controller.searchStudents(tblStudents, kw);
        }
    }

    private void clearForm() {
        txtStudentId.setText("2024-0001");     txtStudentId.setForeground(Color.GRAY);
        txtFirstName.setText("e.g., Juan");    txtFirstName.setForeground(Color.GRAY);
        txtLastName.setText("e.g., Dela Cruz");txtLastName.setForeground(Color.GRAY);
        txtEmail.setText("email@example.com"); txtEmail.setForeground(Color.GRAY);
        txtPhone.setText("09XXXXXXXXX");       txtPhone.setForeground(Color.GRAY);
        txtBirthDate.setText("MM/DD/YYYY");    txtBirthDate.setForeground(Color.GRAY);
        txtBirthPlace.setText("e.g., Manila"); txtBirthPlace.setForeground(Color.GRAY);
        txtAddress.setText("Street, City, Province"); txtAddress.setForeground(Color.GRAY);
        cmbCivilStatus.setSelectedIndex(0);
        cmbSex.setSelectedIndex(0);
        tblStudents.clearSelection();
    }

    private void openStudentDetails(int row) {
        if (row < 0) return;
        String studentId = (String) tblStudents.getValueAt(row, 0);
        Student student = controller.getStudent(studentId);
        if (student != null) {
            new StudentDetailsDialog(student).setVisible(true);
            loadStudentTable();
        }
    }

    private class StudentDetailsDialog extends JDialog {
        private final JTextField fStudentId, fFirstName, fLastName, fEmail, fPhone;
        private final JTextField fBirthDate, fBirthPlace, fAddress;
        private final JComboBox<String> fCivilStatus, fSex;
        private final JButton btnEdit, btnDelete, btnSave, btnCancel;
        private final Student student;
        private final JButton btnRestore;

        public StudentDetailsDialog(Student student) {
            super(SwingUtilities.getWindowAncestor(RegistrationView.this),
                  "Student Details", ModalityType.APPLICATION_MODAL);
            this.student = student;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setPreferredSize(new Dimension(1060, 860));
            setMinimumSize(new Dimension(960, 800));
            setResizable(true);

            JPanel dialogRoot = new JPanel(new GridBagLayout());
            dialogRoot.setBackground(new Color(235, 239, 246));
            dialogRoot.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel card = new JPanel(new BorderLayout(18, 18));
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

            fStudentId  = new JTextField(student.getStudentId());
            fFirstName  = new JTextField(student.getFirstName() != null  ? student.getFirstName()  : "");
            fLastName   = new JTextField(student.getLastName()  != null  ? student.getLastName()   : "");
            fEmail      = new JTextField(student.getEmail()     != null  ? student.getEmail()      : "");
            fPhone      = new JTextField(student.getPhone()     != null  ? student.getPhone()      : "");
            fBirthDate  = new JTextField(student.getBirthDate() != null
                              ? student.getBirthDate().format(DATE_FMT) : "");
            fBirthPlace = new JTextField(student.getBirthPlace() != null ? student.getBirthPlace() : "");
            fAddress    = new JTextField(student.getAddress()    != null ? student.getAddress()    : "");
            fCivilStatus = new JComboBox<>(CIVIL_STATUSES);
            fSex         = new JComboBox<>(SEXES);

            if (student.getCivilStatus() != null) fCivilStatus.setSelectedItem(student.getCivilStatus());
            if (student.getSex()         != null) fSex.setSelectedItem(student.getSex());

            for (JTextField f : new JTextField[]{fStudentId, fFirstName, fLastName,
                                                 fEmail, fPhone, fBirthDate, fBirthPlace, fAddress})
                UIHelper.styleField(f);
            UIHelper.styleComboBox(fCivilStatus);
            UIHelper.styleComboBox(fSex);

            JPanel detailGrid = new JPanel(new GridLayout(0, 2, 10, 8));
            detailGrid.setOpaque(false);
            detailGrid.add(UIHelper.createLabeledField("Student ID",   fStudentId));
            detailGrid.add(UIHelper.createLabeledField("Email",        fEmail));
            detailGrid.add(UIHelper.createLabeledField("First Name",   fFirstName));
            detailGrid.add(UIHelper.createLabeledField("Phone",        fPhone));
            detailGrid.add(UIHelper.createLabeledField("Last Name",    fLastName));
            detailGrid.add(UIHelper.createLabeledField("Birth Date",   fBirthDate));
            detailGrid.add(UIHelper.createLabeledField("Civil Status", fCivilStatus));
            detailGrid.add(UIHelper.createLabeledField("Birth Place",  fBirthPlace));
            detailGrid.add(UIHelper.createLabeledField("Sex",          fSex));
            detailGrid.add(UIHelper.createLabeledField("Address",      fAddress));

            JPanel coursesPanel = new JPanel(new BorderLayout(8, 8));
            coursesPanel.setOpaque(false);
            coursesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                "Enrolled Courses", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)));

            JTable courseTable = new JTable(new DefaultTableModel(
                new Object[]{"Course Code", "Course", "Units", "Tuition", "Paid", "Balance"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            });
            UIHelper.styleTable(courseTable);
            JScrollPane courseScroll = new JScrollPane(courseTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            UIHelper.styleScrollPane(courseScroll);
            courseScroll.setPreferredSize(new Dimension(400, 150));
            coursesPanel.add(courseScroll, BorderLayout.CENTER);

            List<Enrollment> enrollments = enrollmentController.getEnrollmentsByStudent(student.getStudentId());
            DefaultTableModel courseModel = (DefaultTableModel) courseTable.getModel();
            if (enrollments.isEmpty()) {
                courseModel.addRow(new Object[]{"", "No enrolled courses", "", "", "", ""});
            } else {
                for (Enrollment e : enrollments) {
                    double paid = enrollmentController.getTotalPaid(e.getEnrollmentId());
                    double bal = Math.max(0, e.getTotalTuition() - paid);
                    courseModel.addRow(new Object[]{e.getCourseCode(), e.getCourseName(), e.getUnits(), String.format("%.2f", e.getTotalTuition()), String.format("%.2f", paid), String.format("%.2f", bal)});
                }
            }

            btnDelete = UIHelper.createButton("Deactivate", new Color(192, 57, 43));
            btnRestore = UIHelper.createButton("Reactivate", UIHelper.SUCCESS);
            btnEdit = UIHelper.createSecondaryButton("Edit");
            btnSave = UIHelper.createButton("Save", new Color(22, 163, 74));
            btnCancel = UIHelper.createSecondaryButton("Cancel");
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnRestore.setVisible(false);

            btnRestore.addActionListener(e -> {
                controller.reactivateStudent(student.getStudentId());
                dispose();
            });

            btnEdit.addActionListener(e -> setEditMode(true));
            btnDelete.addActionListener(e -> {
                controller.deactivateStudent(student.getStudentId());
                dispose();
            });
            btnSave.addActionListener(e -> {
                LocalDate bd = parseBirthDate(UIHelper.getCleanText(fBirthDate));
                String rawBD = UIHelper.getCleanText(fBirthDate);
                if (!rawBD.isEmpty() && bd == null) return;
                String cs  = (String) fCivilStatus.getSelectedItem();
                String sex = (String) fSex.getSelectedItem();
                controller.updateStudent(
                    student.getStudentId(),
                    UIHelper.getCleanText(fFirstName),
                    UIHelper.getCleanText(fLastName),
                    UIHelper.getCleanText(fEmail),
                    UIHelper.getCleanText(fPhone),
                    bd,
                    UIHelper.getCleanText(fBirthPlace),
                    cs  == null ? "" : cs,
                    sex == null ? "" : sex,
                    UIHelper.getCleanText(fAddress));
                setEditMode(false);
                dispose();
            });
            btnCancel.addActionListener(e -> { setEditMode(false); loadDetails(); });

            JPanel editBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
            editBar.setOpaque(false);
            editBar.add(btnEdit); editBar.add(btnSave); editBar.add(btnCancel);

            JPanel detailWrapper = new JPanel(new GridBagLayout());
            detailWrapper.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            detailWrapper.add(detailGrid, gbc);
            gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
            detailWrapper.add(editBar, gbc);
            gbc.gridy = 2; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
            detailWrapper.add(Box.createGlue(), gbc);
            card.add(detailWrapper, BorderLayout.CENTER);

            JPanel deleteBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            deleteBar.setOpaque(false);
            if (!student.isActive()) {
                btnDelete.setVisible(false);
                btnRestore.setVisible(true);
                deleteBar.add(btnRestore);
            } else {
                deleteBar.add(btnDelete);
            }

            JPanel bottomWrapper = new JPanel(new BorderLayout(8, 8));
            bottomWrapper.setOpaque(false);
            bottomWrapper.add(coursesPanel, BorderLayout.CENTER);
            bottomWrapper.add(deleteBar, BorderLayout.SOUTH);
            card.add(bottomWrapper, BorderLayout.SOUTH);

            setEditMode(false);
            GridBagConstraints cardGbc = new GridBagConstraints();
            cardGbc.fill = GridBagConstraints.BOTH;
            cardGbc.weightx = 1.0;
            cardGbc.weighty = 1.0;
            dialogRoot.add(card, cardGbc);
            setContentPane(dialogRoot);
            pack();
            setLocationRelativeTo(getOwner());
        }

        private void loadDetails() {
            fStudentId.setText(student.getStudentId());
            fFirstName.setText(student.getFirstName()  != null ? student.getFirstName()  : "");
            fLastName.setText(student.getLastName()    != null ? student.getLastName()   : "");
            fEmail.setText(student.getEmail()          != null ? student.getEmail()      : "");
            fPhone.setText(student.getPhone()          != null ? student.getPhone()      : "");
            fBirthDate.setText(student.getBirthDate()  != null
                ? student.getBirthDate().format(DATE_FMT) : "");
            fBirthPlace.setText(student.getBirthPlace() != null ? student.getBirthPlace() : "");
            fAddress.setText(student.getAddress()      != null ? student.getAddress()    : "");
            if (student.getCivilStatus() != null) fCivilStatus.setSelectedItem(student.getCivilStatus());
            else fCivilStatus.setSelectedIndex(0);
            if (student.getSex() != null) fSex.setSelectedItem(student.getSex());
            else fSex.setSelectedIndex(0);
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

        private void styleComboMode(JComboBox<String> combo, boolean editable) {
            combo.setEnabled(editable);
            combo.setFocusable(editable);
            if (!editable) {
                combo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                combo.setBackground(Color.WHITE);
                combo.setOpaque(false);
            } else {
                UIHelper.styleComboBox(combo);
                combo.setOpaque(true);
            }
        }

        private void setEditMode(boolean editable) {
            styleFieldMode(fStudentId,  false);
            styleFieldMode(fFirstName,  editable);
            styleFieldMode(fLastName,   editable);
            styleFieldMode(fEmail,      editable);
            styleFieldMode(fPhone,      editable);
            styleFieldMode(fBirthDate,  editable);
            styleFieldMode(fBirthPlace, editable);
            styleFieldMode(fAddress,    editable);
            styleComboMode(fCivilStatus, editable);
            styleComboMode(fSex,         editable);
            btnEdit.setVisible(!editable);
            btnDelete.setVisible(!editable);
            btnSave.setVisible(editable);
            btnCancel.setVisible(editable);
        }
    }
}