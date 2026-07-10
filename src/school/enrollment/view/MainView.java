package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class MainView extends JFrame {
    private RegistrationView registrationView;
    private CourseView courseView;
    private EnrollmentView enrollmentView;
    private PaymentView paymentView;

    public MainView() {
        setTitle("Online School Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();

        registrationView = new RegistrationView();
        courseView = new CourseView();
        enrollmentView = new EnrollmentView();
        paymentView = new PaymentView();

        tabs.addTab("Student Registration", new ImageIcon(), registrationView, "Register and manage students");
        tabs.addTab("Course Management", new ImageIcon(), courseView, "Add and manage courses");
        tabs.addTab("Enrollment", new ImageIcon(), enrollmentView, "Enroll students in courses");
        tabs.addTab("Tuition Payment", new ImageIcon(), paymentView, "Process tuition payments");

        tabs.addChangeListener((ChangeEvent e) -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 2) {
                enrollmentView.loadComboBoxes();
                enrollmentView.loadEnrollments();
            } else if (idx == 3) {
                paymentView.loadStudentCombo();
                paymentView.loadStudentEnrollments();
            }
        });

        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 14f));
        tabs.setTabPlacement(JTabbedPane.TOP);

        add(tabs, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("Online School Enrollment System v1.0 | MVC + DAO Architecture"));
        add(statusBar, BorderLayout.SOUTH);
    }
}
