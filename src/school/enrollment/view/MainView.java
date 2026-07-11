package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class MainView extends JFrame {
    private RegistrationView registrationView;
    private CourseView courseView;
    private EnrollmentView enrollmentView;
    private PaymentView paymentView;
    private InfoView infoView;

    public MainView() {
        setTitle("Online School Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setLocationRelativeTo(null);
        setIconImage(null);

        try {
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TabbedPane.selected", new Color(41, 128, 185));
            UIManager.put("TabbedPane.contentAreaColor", new Color(245, 246, 250));
        } catch (Exception e) { /* ignore */ }

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(236, 240, 241));
        tabs.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));

        registrationView = new RegistrationView();
        courseView = new CourseView();
        enrollmentView = new EnrollmentView();
        paymentView = new PaymentView();
        infoView = new InfoView();

        tabs.addTab("  Student Registration  ", registrationView);
        tabs.addTab("  Course Management  ", courseView);
        tabs.addTab("  Enrollment  ", enrollmentView);
        tabs.addTab("  Tuition Payment  ", paymentView);
        tabs.addTab("  Info  ", infoView);

        tabs.addChangeListener((ChangeEvent e) -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 2) {
                enrollmentView.loadComboBoxes();
                enrollmentView.loadEnrollments();
            } else if (idx == 3) {
                paymentView.loadStudentCombo();
                paymentView.loadStudentEnrollments();
                paymentView.loadPaymentHistory();
            }
        });

        add(tabs, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        statusBar.setBackground(UIHelper.HEADER_BG);
        JLabel statusLabel = new JLabel("  Online School Enrollment System v2.0  |  MVC + DAO Architecture  |  Group 2");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(UIHelper.HEADER_FG);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }
}
