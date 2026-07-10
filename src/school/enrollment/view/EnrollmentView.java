package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = createEnrollmentForm();
        JPanel tablePanel = createTablePanel();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tablePanel);
        split.setResizeWeight(0.25);
        split.setDividerSize(5);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createEnrollmentForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Enroll Student in Course"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbStudent = new JComboBox<>();
        cmbCourse = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(cmbStudent, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Course:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(cmbCourse, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnEnroll = new JButton("Enroll");
        JButton btnCancel = new JButton("Cancel Enrollment");
        JButton btnRefresh = new JButton("Refresh Lists");

        btnEnroll.addActionListener(e -> {
            Student s = (Student) cmbStudent.getSelectedItem();
            Course c = (Course) cmbCourse.getSelectedItem();
            if (s == null || c == null) {
                JOptionPane.showMessageDialog(this, "Please select both a student and a course.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            enrollmentController.enrollStudent(s.getStudentId(), c.getCourseId());
            loadEnrollments();
        });

        btnCancel.addActionListener(e -> {
            int row = tblEnrollments.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an enrollment from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int enrollmentId = (int) tblEnrollments.getValueAt(row, 0);
            enrollmentController.cancelEnrollment(enrollmentId);
            loadEnrollments();
        });

        btnRefresh.addActionListener(e -> {
            loadComboBoxes();
            loadEnrollments();
        });

        buttons.add(btnEnroll);
        buttons.add(btnCancel);
        buttons.add(btnRefresh);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Enrollment Records"));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> enrollmentController.searchEnrollments(tblEnrollments, txtSearch.getText()));
        txtSearch.addActionListener(e -> enrollmentController.searchEnrollments(tblEnrollments, txtSearch.getText()));
        JButton btnRefresh = new JButton("Show All");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadEnrollments();
        });
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblEnrollments = new JTable(new DefaultTableModel(new Object[]{"ID", "Student", "Course Code", "Course", "Units", "Total Tuition", "Paid", "Balance", "Status", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
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
