package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import school.enrollment.controller.EnrollmentController;
import school.enrollment.controller.StudentController;
import school.enrollment.controller.CourseController;
import school.enrollment.model.Student;
import school.enrollment.model.Course;

public class EnrollmentView extends JPanel {
    private final EnrollmentController enrollmentController;
    private final StudentController studentController;
    private final CourseController courseController;
    private JComboBox<Student> cmbStudent;
    private JComboBox<Course> cmbCourse;
    private JTable tblEnrollments;
    private JTextField txtSearch;

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
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.25);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setTopComponent(createEnrollmentForm());
        split.setBottomComponent(createTablePanel());
        add(split, BorderLayout.CENTER);
    }

    private JPanel createEnrollmentForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIHelper.createBorder("Enroll Student in Course"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridBagLayout());
        UIHelper.stylePanel(fields);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbStudent = new JComboBox<>();
        cmbStudent.setFont(UIHelper.MAIN_FONT);
        cmbCourse = new JComboBox<>();
        cmbCourse.setFont(UIHelper.MAIN_FONT);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l1 = new JLabel("Student:");
        UIHelper.styleLabel(l1); fields.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(cmbStudent, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l2 = new JLabel("Course:");
        UIHelper.styleLabel(l2); fields.add(l2, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(cmbCourse, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnEnroll = UIHelper.createButton("Enroll", UIHelper.SUCCESS);
        JButton btnCancel = UIHelper.createButton("Cancel Enrollment", new Color(192, 57, 43));
        JButton btnRefresh = UIHelper.createButton("Refresh Lists", UIHelper.ACCENT);

        btnEnroll.addActionListener(e -> {
            Student s = (Student) cmbStudent.getSelectedItem();
            Course c = (Course) cmbCourse.getSelectedItem();
            if (s == null || c == null) {
                JOptionPane.showMessageDialog(this, "Select a student and a course.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            enrollmentController.enrollStudent(s.getStudentId(), c.getCourseId());
            loadEnrollments();
        });
        btnCancel.addActionListener(e -> {
            int row = tblEnrollments.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an enrollment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            enrollmentController.cancelEnrollment((int) tblEnrollments.getValueAt(row, 0));
            loadEnrollments();
        });
        btnRefresh.addActionListener(e -> { loadComboBoxes(); loadEnrollments(); });

        buttons.add(btnEnroll); buttons.add(btnCancel); buttons.add(btnRefresh);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Enrollment Records"));
        UIHelper.stylePanel(panel);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        UIHelper.stylePanel(searchPanel);
        txtSearch = new JTextField();
        UIHelper.styleField(txtSearch);
        JLabel sl = new JLabel("Search: ");
        UIHelper.styleLabel(sl);
        searchPanel.add(sl, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createButton("Search", UIHelper.ACCENT);
        JButton btnRefresh = UIHelper.createButton("Show All", UIHelper.ACCENT);
        btnSearch.addActionListener(e -> enrollmentController.searchEnrollments(tblEnrollments, txtSearch.getText()));
        txtSearch.addActionListener(e -> enrollmentController.searchEnrollments(tblEnrollments, txtSearch.getText()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadEnrollments(); });
        btnPanel.add(btnSearch); btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblEnrollments = new JTable(new DefaultTableModel(
            new Object[]{"ID", "Student", "Course Code", "Course", "Units", "Total Tuition", "Paid", "Balance", "Status", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblEnrollments);
        loadEnrollments();

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);
        return panel;
    }

    public void loadComboBoxes() {
        cmbStudent.removeAllItems();
        cmbCourse.removeAllItems();
        try {
            for (Student s : studentController.getAllStudents()) cmbStudent.addItem(s);
            for (Course c : courseController.getAllCourses()) cmbCourse.addItem(c);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading lists: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadEnrollments() {
        enrollmentController.loadEnrollments(tblEnrollments);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadComboBoxes();
        loadEnrollments();
    }
}
