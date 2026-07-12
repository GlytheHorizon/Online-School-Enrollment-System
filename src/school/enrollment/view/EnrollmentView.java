package school.enrollment.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import school.enrollment.controller.EnrollmentController;
import school.enrollment.controller.StudentController;
import school.enrollment.controller.CourseController;
import school.enrollment.model.Student;
import school.enrollment.model.Course;
import school.enrollment.model.Enrollment;

public class EnrollmentView extends JPanel {
    private final EnrollmentController enrollmentController;
    private final StudentController studentController;
    private final CourseController courseController;
    private JComboBox<Student> cmbStudent;
    private JButton btnCourseDropdown;
    private JPopupMenu coursePopup;
    private JPanel courseFieldPanel;
    private JPanel selectedCoursePanel;
    private JLabel lblCourseSelected;
    private JLabel lblCourseDisabled;
    private List<Course> selectedCourses = new ArrayList<>();
    private JTable tblEnrollments, tblAudit;
    private JTextField txtSearch, txtAuditSearch;

    public EnrollmentView() {
        enrollmentController = new EnrollmentController();
        studentController = new StudentController();
        courseController = new CourseController();
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        add(createEnrollmentForm(), BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.5);
        mainSplit.setDividerSize(6);
        mainSplit.setBorder(null);
        mainSplit.setTopComponent(createEnrollmentTable());
        mainSplit.setBottomComponent(createAuditTable());
        add(mainSplit, BorderLayout.CENTER);
    }

    private JPanel createEnrollmentForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIHelper.createBorder("Enroll Student in Course"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridLayout(0, 2, 18, 12));
        UIHelper.stylePanel(fields);

        cmbStudent = new JComboBox<>();
        UIHelper.styleComboBox(cmbStudent);
        UIHelper.setComboPlaceholder(cmbStudent, "Select student...");
        cmbStudent.addActionListener(e -> {
            selectedCourses.clear();
            updateCourseDropdownText();
            loadCourseList((Student) cmbStudent.getSelectedItem());
        });

        final JPanel innerCourseField = new JPanel(new BorderLayout(8, 0));
        UIHelper.styleRoundedField(innerCourseField);
        innerCourseField.setPreferredSize(new Dimension(200, 36));
        innerCourseField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnCourseDropdown = new JButton("▼");
        btnCourseDropdown.setFont(UIHelper.MAIN_FONT.deriveFont(Font.BOLD, 12f));
        btnCourseDropdown.setForeground(UIHelper.LABEL_FG);
        btnCourseDropdown.setOpaque(false);
        btnCourseDropdown.setFocusPainted(false);
        btnCourseDropdown.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        btnCourseDropdown.setContentAreaFilled(false);
        btnCourseDropdown.setHorizontalAlignment(SwingConstants.CENTER);
        btnCourseDropdown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCourseDropdown.addActionListener(e -> {
            if (coursePopup != null && coursePopup.getComponentCount() > 0) {
                coursePopup.setPreferredSize(new Dimension(innerCourseField.getWidth(), coursePopup.getPreferredSize().height));
                coursePopup.show(innerCourseField, 0, innerCourseField.getHeight());
            }
        });

        coursePopup = new JPopupMenu();
        coursePopup.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER, 1, true));

        lblCourseSelected = new JLabel("Select courses...");
        lblCourseSelected.setFont(UIHelper.MAIN_FONT.deriveFont(Font.ITALIC, 12f));
        lblCourseSelected.setForeground(new Color(148, 163, 184));

        lblCourseDisabled = new JLabel("Enrollment selected: course selection hidden while updating.");
        lblCourseDisabled.setFont(UIHelper.MAIN_FONT);
        lblCourseDisabled.setForeground(new Color(148, 163, 184));
        lblCourseDisabled.setVisible(false);

        selectedCoursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        selectedCoursePanel.setOpaque(false);
        selectedCoursePanel.setVisible(false);

        JPanel courseContent = new JPanel(new BorderLayout());
        courseContent.setOpaque(false);
        courseContent.add(lblCourseSelected, BorderLayout.CENTER);

        innerCourseField.add(courseContent, BorderLayout.CENTER);
        innerCourseField.add(btnCourseDropdown, BorderLayout.EAST);
        innerCourseField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        innerCourseField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                btnCourseDropdown.doClick();
            }
        });

        JPanel courseFooter = new JPanel(new BorderLayout(6, 6));
        courseFooter.setOpaque(false);
        courseFooter.add(selectedCoursePanel, BorderLayout.CENTER);
        courseFooter.add(lblCourseDisabled, BorderLayout.SOUTH);

        courseFieldPanel = new JPanel(new BorderLayout(4, 4));
        courseFieldPanel.setOpaque(false);
        courseFieldPanel.add(innerCourseField, BorderLayout.CENTER);
        courseFieldPanel.add(courseFooter, BorderLayout.SOUTH);

        fields.add(UIHelper.createLabeledField("Student", cmbStudent));
        fields.add(UIHelper.createLabeledField("Courses", courseFieldPanel));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnEnroll = UIHelper.createButton("Enroll", UIHelper.SUCCESS);
        JButton btnRefresh = UIHelper.createOutlineButton("Refresh Lists", UIHelper.ACCENT);

        btnEnroll.addActionListener(e -> {
            Student s = (Student) cmbStudent.getSelectedItem();
            if (s == null || s.getStudentId() == null || s.getStudentId().isEmpty() || selectedCourses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a student and one or more courses.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (Course c : new ArrayList<>(selectedCourses)) {
                enrollmentController.enrollStudent(s.getStudentId(), c.getCourseId());
            }
            selectedCourses.clear();
            updateCourseDropdownText();
            loadEnrollments(); loadAuditLog();
        });
        btnRefresh.addActionListener(e -> { loadComboBoxes(); loadEnrollments(); loadAuditLog(); });

        buttons.add(btnEnroll); buttons.add(btnRefresh);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }


    private JPanel createEnrollmentTable() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Active Enrollments"));
        UIHelper.stylePanel(panel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        UIHelper.stylePanel(searchPanel);
        txtSearch = new JTextField();
        UIHelper.styleField(txtSearch);
        UIHelper.setPlaceholder(txtSearch, "Search enrollments...");
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Show All");
        tblEnrollments = new JTable(new DefaultTableModel(
            new Object[]{"", "Student ID", "Student Name", "Course Code", "Course", "Units", "Total Tuition", "Paid", "Balance", "Status", "Date", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col == 11) {
                    Object firstColVal = getValueAt(row, 0);
                    return firstColVal instanceof Enrollment;
                }
                return false;
            }
        });
        tblEnrollments.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCourseSelectionVisibility();
                int row = tblEnrollments.getSelectedRow();
                if (row >= 0) {
                    Object firstColVal = tblEnrollments.getValueAt(row, 0);
                    if (firstColVal instanceof StudentGroup) {
                        StudentGroup sg = (StudentGroup) firstColVal;
                        selectStudentInComboBox(sg.getStudentId());
                    }
                }
            }
        });
        UIHelper.styleTable(tblEnrollments);

        // Customize column sizes
        tblEnrollments.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblEnrollments.getColumnModel().getColumn(0).setMaxWidth(30);
        tblEnrollments.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblEnrollments.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(4).setPreferredWidth(180);
        tblEnrollments.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblEnrollments.getColumnModel().getColumn(6).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(7).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(8).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(9).setPreferredWidth(80);
        tblEnrollments.getColumnModel().getColumn(10).setPreferredWidth(90);
        tblEnrollments.getColumnModel().getColumn(11).setPreferredWidth(100);

        tblEnrollments.getColumnModel().getColumn(11).setCellRenderer(new ActionButtonRenderer());
        tblEnrollments.getColumnModel().getColumn(11).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Custom cell renderer
        tblEnrollments.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object firstColVal = table.getModel().getValueAt(row, 0);
                boolean isParent = firstColVal instanceof StudentGroup;

                if (isParent) {
                    c.setFont(UIHelper.BOLD_FONT);
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                    } else {
                        c.setBackground(new Color(241, 245, 249));
                    }
                } else {
                    c.setFont(UIHelper.MAIN_FONT);
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                if (column == 0) {
                    if (isParent) {
                        StudentGroup sg = (StudentGroup) firstColVal;
                        setText(sg.isExpanded() ? "  ▼" : "  ▶");
                    } else {
                        setText("");
                    }
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                // Layout styling
                if (isParent) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)
                    ));
                    // Clear non-relevant columns for parent
                    if (column != 0 && column != 1 && column != 2 && column != 9) {
                        setText("");
                    }
                } else {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                        BorderFactory.createEmptyBorder(0, 16, 0, 8)
                    ));
                    // Clear parent identifiers for child row
                    if (column == 1 || column == 2) {
                        setText("");
                    }
                }
                return c;
            }
        });

        // Add mouse listener to toggle rows on expand button click or double click
        tblEnrollments.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tblEnrollments.rowAtPoint(e.getPoint());
                int col = tblEnrollments.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    Object val = tblEnrollments.getValueAt(row, 0);
                    if (val instanceof StudentGroup) {
                        StudentGroup group = (StudentGroup) val;
                        if (col == 0 || e.getClickCount() == 2) {
                            group.setExpanded(!group.isExpanded());
                            rebuildTable();
                        }
                    }
                }
            }
        });

        JLabel emptyEnrollments = UIHelper.createEmptyStateLabel("No enrollments found.");
        txtSearch.addActionListener(e -> {
            List<Enrollment> list = enrollmentController.getSearchEnrollments(UIHelper.getCleanText(txtSearch));
            loadEnrollmentsList(list);
            UIHelper.setEmptyStateVisible(tblEnrollments, emptyEnrollments);
            updateCourseSelectionVisibility();
        });
        btnSearch.addActionListener(e -> {
            List<Enrollment> list = enrollmentController.getSearchEnrollments(UIHelper.getCleanText(txtSearch));
            loadEnrollmentsList(list);
            UIHelper.setEmptyStateVisible(tblEnrollments, emptyEnrollments);
            updateCourseSelectionVisibility();
        });
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadEnrollments();
            UIHelper.setEmptyStateVisible(tblEnrollments, emptyEnrollments);
        });
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);
        loadEnrollments();
        UIHelper.setEmptyStateVisible(tblEnrollments, emptyEnrollments);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(UIHelper.createTableWithOverlay(tblEnrollments, emptyEnrollments), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAuditTable() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Enrollment History (Audit Log)"));
        UIHelper.stylePanel(panel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        UIHelper.stylePanel(searchPanel);
        txtAuditSearch = new JTextField();
        UIHelper.styleField(txtAuditSearch);
        UIHelper.setPlaceholder(txtAuditSearch, "Search audit log...");
        searchPanel.add(txtAuditSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Show All");
        txtAuditSearch.addActionListener(e -> enrollmentController.searchAuditLog(tblAudit, UIHelper.getCleanText(txtAuditSearch)));
        btnSearch.addActionListener(e -> enrollmentController.searchAuditLog(tblAudit, UIHelper.getCleanText(txtAuditSearch)));
        btnRefresh.addActionListener(e -> { txtAuditSearch.setText(""); loadAuditLog(); });
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblAudit = new JTable(new DefaultTableModel(
            new Object[]{"Audit ID", "Student", "Course Code", "Course", "Action", "Date & Time"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblAudit);
        loadAuditLog();

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblAudit), BorderLayout.CENTER);
        return panel;
    }

    public void loadComboBoxes() {
        cmbStudent.removeAllItems();
        selectedCourses.clear();
        cmbStudent.addItem(new Student("", "", "", "", "", "") {
            @Override
            public String toString() {
                return "";
            }
        });
        try {
            for (Student s : studentController.getAllStudents()) cmbStudent.addItem(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lists: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        loadCourseList(null);
    }

    private List<StudentGroup> studentGroups = new ArrayList<>();

    public void loadEnrollments() {
        List<Enrollment> list = enrollmentController.getAllEnrollments();
        loadEnrollmentsList(list);
        updateCourseSelectionVisibility();
    }

    private void loadEnrollmentsList(List<Enrollment> list) {
        Set<String> expandedStudentIds = new HashSet<>();
        for (StudentGroup sg : studentGroups) {
            if (sg.isExpanded()) {
                expandedStudentIds.add(sg.getStudentId());
            }
        }

        studentGroups.clear();
        java.util.Map<String, StudentGroup> map = new java.util.LinkedHashMap<>();
        for (Enrollment e : list) {
            StudentGroup sg = map.get(e.getStudentId());
            if (sg == null) {
                sg = new StudentGroup(e.getStudentId(), e.getStudentName(), e.getStatus());
                if (expandedStudentIds.contains(e.getStudentId())) {
                    sg.setExpanded(true);
                }
                map.put(e.getStudentId(), sg);
                studentGroups.add(sg);
            }
            sg.getEnrollments().add(e);
        }
        rebuildTable();
    }

    private void rebuildTable() {
        DefaultTableModel model = (DefaultTableModel) tblEnrollments.getModel();
        model.setRowCount(0);

        for (StudentGroup group : studentGroups) {
            // Compute totals for the student
            double totalTuition = 0;
            double totalPaid = 0;
            int totalUnits = 0;
            for (Enrollment e : group.getEnrollments()) {
                totalUnits += e.getUnits();
                totalTuition += e.getTotalTuition();
                totalPaid += enrollmentController.getTotalPaid(e.getEnrollmentId());
            }
            double totalBalance = totalTuition - totalPaid;

            // Add parent row
            model.addRow(new Object[]{
                group, // col 0: StudentGroup object
                group.getStudentId(),
                group.getStudentName(),
                "", // Course Code
                "", // Course
                totalUnits,
                String.format("%.2f", totalTuition),
                String.format("%.2f", totalPaid),
                String.format("%.2f", totalBalance),
                group.getStatus(),
                "",  // Date
                ""   // Action
            });

            if (group.isExpanded()) {
                for (Enrollment e : group.getEnrollments()) {
                    double paid = enrollmentController.getTotalPaid(e.getEnrollmentId());
                    double balance = e.getTotalTuition() - paid;
                    model.addRow(new Object[]{
                        e, // col 0: Enrollment object
                        "", // Student ID
                        "", // Student Name
                        e.getCourseCode(),
                        e.getCourseName(),
                        e.getUnits(),
                        String.format("%.2f", e.getTotalTuition()),
                        String.format("%.2f", paid),
                        String.format("%.2f", balance),
                        e.getStatus(),
                        e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : "",
                        "" // Action
                    });
                }
            }
        }
    }

    private static class StudentGroup {
        private final String studentId;
        private final String studentName;
        private final String status;
        private final List<Enrollment> enrollments = new ArrayList<>();
        private boolean expanded = false;

        public StudentGroup(String studentId, String studentName, String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.status = status;
        }

        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getStatus() { return status; }
        public List<Enrollment> getEnrollments() { return enrollments; }
        public boolean isExpanded() { return expanded; }
        public void setExpanded(boolean expanded) { this.expanded = expanded; }
    }


    public void loadAuditLog() {
        enrollmentController.loadAuditLog(tblAudit);
    }

    private void loadCourseList(Student selectedStudent) {
        coursePopup.removeAll();
        selectedCourses.clear();
        try {
            Set<Integer> enrolledCourseIds = new HashSet<>();
            if (selectedStudent != null && selectedStudent.getStudentId() != null && !selectedStudent.getStudentId().isEmpty()) {
                for (Enrollment e : enrollmentController.getEnrollmentsByStudent(selectedStudent.getStudentId())) {
                    enrolledCourseIds.add(e.getCourseId());
                }
            }
            for (Course c : courseController.getAllCourses()) {
                if (selectedStudent == null || selectedStudent.getStudentId().isEmpty() || !enrolledCourseIds.contains(c.getCourseId())) {
                    JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.toString());
                    item.setFont(UIHelper.MAIN_FONT);
                    item.setBackground(Color.WHITE);
                    item.setOpaque(true);
                    item.addActionListener(evt -> {
                        if (item.isSelected()) selectedCourses.add(c);
                        else selectedCourses.remove(c);
                        updateCourseDropdownText();
                    });
                    coursePopup.add(item);
                }
            }
                if (coursePopup.getComponentCount() == 0) {
                lblCourseSelected.setText("No courses available");
                lblCourseSelected.setFont(UIHelper.MAIN_FONT.deriveFont(Font.ITALIC, 12f));
                lblCourseSelected.setForeground(new Color(148, 163, 184));
            } else {
                lblCourseSelected.setText("Select courses...");
                lblCourseSelected.setFont(UIHelper.MAIN_FONT.deriveFont(Font.ITALIC, 12f));
                lblCourseSelected.setForeground(new Color(148, 163, 184));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading course list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateCourseDropdownText();
        updateCourseSelectionVisibility();
    }

    private void updateCourseDropdownText() {
        selectedCoursePanel.removeAll();
        if (selectedCourses.isEmpty()) {
            lblCourseSelected.setText("Select courses...");
            lblCourseSelected.setFont(UIHelper.MAIN_FONT.deriveFont(Font.ITALIC, 12f));
            lblCourseSelected.setForeground(new Color(148, 163, 184));
            selectedCoursePanel.setVisible(false);
        } else {
            lblCourseSelected.setText(selectedCourses.size() + " course" + (selectedCourses.size() == 1 ? " selected" : "s selected"));
            lblCourseSelected.setFont(UIHelper.MAIN_FONT);
            lblCourseSelected.setForeground(UIHelper.LABEL_FG);
            for (Course selectedCourse : selectedCourses) {
                selectedCoursePanel.add(UIHelper.createTagChip(
                    selectedCourse.getCourseCode(),
                    selectedCourse.getCourseName(),
                    () -> {
                        selectedCourses.remove(selectedCourse);
                        if (coursePopup != null) {
                            for (Component comp : coursePopup.getComponents()) {
                                if (comp instanceof JCheckBoxMenuItem && ((JCheckBoxMenuItem) comp).getText().equals(selectedCourse.toString())) {
                                    ((JCheckBoxMenuItem) comp).setSelected(false);
                                }
                            }
                        }
                        updateCourseDropdownText();
                    }
                ));
            }
            selectedCoursePanel.setVisible(true);
        }
        selectedCoursePanel.revalidate();
        selectedCoursePanel.repaint();
    }

    private void updateCourseSelectionVisibility() {
        boolean enrollmentSelected = tblEnrollments.getSelectedRow() >= 0;
        btnCourseDropdown.setVisible(!enrollmentSelected);
        lblCourseDisabled.setVisible(enrollmentSelected);
        if (enrollmentSelected) {
            selectedCourses.clear();
            updateCourseDropdownText();
        }
        courseFieldPanel.revalidate();
        courseFieldPanel.repaint();
    }

    private void selectStudentInComboBox(String studentId) {
        if (studentId == null) return;
        for (int i = 0; i < cmbStudent.getItemCount(); i++) {
            Student s = cmbStudent.getItemAt(i);
            if (s != null && studentId.equals(s.getStudentId())) {
                cmbStudent.setSelectedItem(s);
                break;
            }
        }
    }

    private class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton button;
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 4));
            setOpaque(false);
            button = new JButton("Cancel");
            button.setFont(UIHelper.MAIN_FONT.deriveFont(Font.BOLD, 11f));
            button.setForeground(Color.WHITE);
            button.setBackground(UIHelper.DANGER);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(80, 24));
            add(button);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Object firstColVal = table.getModel().getValueAt(row, 0);
            if (firstColVal instanceof Enrollment) {
                button.setVisible(true);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setOpaque(true);
                } else {
                    setOpaque(false);
                }
                return this;
            } else {
                JLabel lbl = new JLabel("");
                lbl.setOpaque(true);
                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                } else {
                    lbl.setBackground(new Color(241, 245, 249));
                }
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)
                ));
                return lbl;
            }
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton button;
        private Enrollment currentEnrollment;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            setClickCountToStart(1);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            panel.setOpaque(true);
            button = new JButton("Cancel");
            button.setFont(UIHelper.MAIN_FONT.deriveFont(Font.BOLD, 11f));
            button.setForeground(Color.WHITE);
            button.setBackground(UIHelper.DANGER);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(80, 24));
            panel.add(button);
            
            button.addActionListener(e -> {
                fireEditingStopped();
                if (currentEnrollment != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                        button.getParent(),
                        "Are you sure you want to cancel the enrollment of " + currentEnrollment.getStudentName() + " in " + currentEnrollment.getCourseName() + "?",
                        "Confirm Cancel Enrollment",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        enrollmentController.cancelEnrollment(currentEnrollment.getEnrollmentId());
                        loadEnrollments();
                        loadAuditLog();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            Object firstColVal = table.getModel().getValueAt(row, 0);
            if (firstColVal instanceof Enrollment) {
                currentEnrollment = (Enrollment) firstColVal;
                panel.setBackground(table.getSelectionBackground());
                return panel;
            }
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return "Cancel";
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadComboBoxes();
        loadEnrollments();
        loadAuditLog();
    }
}
