package school.enrollment.view;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class MainView extends JFrame {
    private DashboardView dashboardView;
    private RegistrationView registrationView;
    private CourseView courseView;
    private EnrollmentView enrollmentView;
    private PaymentView paymentView;
    private InfoView infoView;
    private boolean fullscreen = false;

    public MainView() {
        setTitle("Online School Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 820);
        setLocationRelativeTo(null);
        setIconImage(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        fullscreen = true;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F11) {
                toggleFullscreen();
                return true;
            }
            return false;
        });

        try {
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TabbedPane.selected", new Color(37, 99, 235));
            UIManager.put("TabbedPane.contentAreaColor", new Color(248, 250, 252));
            UIManager.put("TabbedPane.foreground", new Color(71, 85, 105));
        } catch (Exception e) {
            /* ignore */ }

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("School Enrollment Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Student records and billing management");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(226, 232, 240));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(241, 245, 249));
        tabs.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        dashboardView    = new DashboardView();
        registrationView = new RegistrationView();
        courseView       = new CourseView();
        enrollmentView   = new EnrollmentView();
        paymentView      = new PaymentView();
        infoView         = new InfoView();

        JScrollPane dashScroll   = createScrollPane(dashboardView);
        JScrollPane regScroll    = createScrollPane(registrationView);
        JScrollPane courseScroll = createScrollPane(courseView);
        JScrollPane enrollScroll = createScrollPane(enrollmentView);
        JScrollPane paymentScroll = createScrollPane(paymentView);
        JScrollPane infoScroll   = createScrollPane(infoView);

        tabs.addTab("  Dashboard  ", dashScroll);
        tabs.addTab("  Student Registration  ", regScroll);
        tabs.addTab("  Course Management  ", courseScroll);
        tabs.addTab("  Enrollment  ", enrollScroll);
        tabs.addTab("  Tuition Payment  ", paymentScroll);
        tabs.addTab("  Info  ", infoScroll);

        tabs.addChangeListener((ChangeEvent e) -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) {
                dashboardView.refresh();
            } else if (idx == 3) {
                enrollmentView.loadComboBoxes();
                enrollmentView.loadEnrollments();
            } else if (idx == 4) {
                paymentView.loadStudentCombo();
                paymentView.loadStudentEnrollments();
                paymentView.loadPaymentHistory();
            }
        });

        root.add(tabs, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        statusBar.setBackground(new Color(30, 41, 59));
        JLabel statusLabel = new JLabel(
                "  Online School Enrollment System v4.0  |  MVC + DAO Architecture  |  Group 2");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel);
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(24);
        UIHelper.styleScrollPane(sp);
        return sp;
    }

    private void toggleFullscreen() {
        fullscreen = !fullscreen;
        if (fullscreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
            setSize(1200, 820);
            setLocationRelativeTo(null);
        }
    }
}
