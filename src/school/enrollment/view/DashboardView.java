package school.enrollment.view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import school.enrollment.controller.DashboardController;
import school.enrollment.model.Payment;

public class DashboardView extends JPanel {

    private final DashboardController controller = new DashboardController();

    private JLabel studentsValue, studentsSub;
    private JLabel coursesValue, coursesSub;
    private JLabel enrollmentsValue, enrollmentsSub;
    private JLabel revenueValue, revenueSub;

    private JPanel chartPanel;
    private BarChartPanel enrollmentChart;
    private LineChartPanel revenueChart;
    private DefaultTableModel recentModel;
    private JTable recentTable;
    private JLabel emptyLabel;

    private static final Color BG        = new Color(246, 248, 251);
    private static final Color BORDER    = new Color(226, 232, 240);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTE = new Color(100, 116, 139);
    private static final Color GREEN     = new Color(22, 163, 74);
    private static final Color BLUE      = new Color(37, 99, 235);
    private static final Color PURPLE    = new Color(124, 58, 237);
    private static final Color AMBER     = new Color(245, 158, 11);
    private static final Color RED       = new Color(220, 38, 38);

    public DashboardView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildHeader());
        content.add(Box.createVerticalStrut(20));
        content.add(buildStatCards());
        content.add(Box.createVerticalStrut(20));
        content.add(buildChartSection());
        content.add(Box.createVerticalStrut(20));
        content.add(buildRecentSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    // ───────────────────────── Header ─────────────────────────
    private JPanel buildHeader() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Quick overview of enrollment and tuition activity.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTE);
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        row.add(left, BorderLayout.WEST);
        row.add(refreshBtn, BorderLayout.EAST);
        return row;
    }

    // ───────────────────────── Stat cards ─────────────────────────
    private JPanel buildStatCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        studentsValue = new JLabel("—"); studentsSub = new JLabel(" ");
        coursesValue  = new JLabel("—"); coursesSub  = new JLabel(" ");
        enrollmentsValue = new JLabel("—"); enrollmentsSub = new JLabel(" ");
        revenueValue = new JLabel("—"); revenueSub = new JLabel(" ");

        cards.add(statCard("Total Students",  studentsValue,    studentsSub,    BLUE,   "👥"));
        cards.add(statCard("Active Courses",  coursesValue,     coursesSub,     GREEN,  "📚"));
        cards.add(statCard("Enrollments",     enrollmentsValue, enrollmentsSub, AMBER,  "📝"));
        cards.add(statCard("Total Revenue",   revenueValue,     revenueSub,     PURPLE, "💵"));
        return cards;
    }

    private JPanel statCard(String title, JLabel valueLabel, JLabel subLabel, Color accent, String icon) {
        JPanel card = roundedPanel(14);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 14, 16));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_MUTE);

        JPanel iconBox = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(34, 34));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconBox.add(iconLabel, BorderLayout.CENTER);

        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(iconBox, BorderLayout.EAST);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_DARK);

        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(accent);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(valueLabel);
        bottom.add(subLabel);

        card.add(topRow, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ───────────────────────── Chart ─────────────────────────
    private JPanel buildChartSection() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        row.setPreferredSize(new Dimension(10, 280));

        row.add(chartCard("Enrollment Trends (Last 6 Months)", enrollmentChart = new BarChartPanel()));
        row.add(chartCard("Revenue Overview (Last 6 Months)", revenueChart = new LineChartPanel()));
        return row;
    }

    private JPanel chartCard(String title, JPanel chart) {
        JPanel wrap = roundedPanel(14);
        wrap.setLayout(new BorderLayout(0, 10));
        wrap.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(TEXT_DARK);

        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(10, 200));

        wrap.add(heading, BorderLayout.NORTH);
        wrap.add(chart, BorderLayout.CENTER);
        return wrap;
    }

    /** Simple custom-painted bar chart, no external library needed. */
    private class BarChartPanel extends JPanel {
        private Map<String, Integer> data = Map.of();
        void setData(Map<String, Integer> data) { this.data = data; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int leftPad = 36, bottomPad = 24, topPad = 16, rightPad = 10;
            int plotW = w - leftPad - rightPad;
            int plotH = h - topPad - bottomPad;

            int max = data.values().stream().max(Integer::compareTo).orElse(1);
            if (max == 0) max = 5;
            int niceMax = ((max / 5) + 1) * 5; // round up to nearest 5 for clean gridlines

            // Gridlines + Y labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(226, 232, 240));
            for (int i = 0; i <= 4; i++) {
                int y = topPad + plotH - (int) (plotH * (i / 4.0));
                g2.drawLine(leftPad, y, w - rightPad, y);
                g2.setColor(TEXT_MUTE);
                String lbl = String.valueOf((int) (niceMax * (i / 4.0)));
                g2.drawString(lbl, 2, y + 4);
                g2.setColor(new Color(226, 232, 240));
            }

            int n = Math.max(data.size(), 1);
            int slot = plotW / n;
            int barW = Math.min(44, (int) (slot * 0.55));
            int x = leftPad + (slot - barW) / 2;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                int barH = (int) ((e.getValue() / (double) niceMax) * plotH);
                int y = topPad + plotH - barH;

                g2.setColor(BLUE);
                g2.fillRoundRect(x, y, barW, Math.max(barH, 2), 6, 6);

                g2.setColor(TEXT_DARK);
                String val = String.valueOf(e.getValue());
                int valW = g2.getFontMetrics().stringWidth(val);
                g2.drawString(val, x + barW / 2 - valW / 2, y - 4);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(TEXT_MUTE);
                String label = e.getKey();
                int lblW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + barW / 2 - lblW / 2, h - 6);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

                x += slot;
            }
            g2.dispose();
        }
    }

    private class LineChartPanel extends JPanel {
        private Map<String, Double> data = Map.of();
        void setData(Map<String, Double> data) { this.data = data; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int leftPad = 54, bottomPad = 24, topPad = 16, rightPad = 14;
            int plotW = w - leftPad - rightPad;
            int plotH = h - topPad - bottomPad;

            double max = data.values().stream().max(Double::compareTo).orElse(1.0);
            if (max <= 0) max = 1000;
            double niceMax = Math.ceil(max / 1000.0) * 1000;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                int y = topPad + plotH - (int) (plotH * (i / 4.0));
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(leftPad, y, w - rightPad, y);
                g2.setColor(TEXT_MUTE);
                String lbl = String.format("%,.0f", niceMax * (i / 4.0));
                g2.drawString(lbl, 2, y + 4);
            }

            int n = data.size();
            if (n == 0) { g2.dispose(); return; }
            int slot = n > 1 ? plotW / (n - 1) : plotW;

            int[] xs = new int[n], ys = new int[n];
            int i = 0;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                int x = leftPad + (n > 1 ? slot * i : plotW / 2);
                int y = topPad + plotH - (int) ((e.getValue() / niceMax) * plotH);
                xs[i] = x; ys[i] = y;
                i++;
            }

            g2.setColor(BLUE);
            g2.setStroke(new BasicStroke(2.5f));
            for (int k = 0; k < n - 1; k++) g2.drawLine(xs[k], ys[k], xs[k + 1], ys[k + 1]);

            g2.setColor(Color.WHITE);
            for (int k = 0; k < n; k++) {
                g2.setColor(BLUE);
                g2.fillOval(xs[k] - 4, ys[k] - 4, 8, 8);
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[k] - 2, ys[k] - 2, 4, 4);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTE);
            i = 0;
            for (String label : data.keySet()) {
                int lblW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, xs[i] - lblW / 2, h - 6);
                i++;
            }
            g2.dispose();
        }
    }

    // ───────────────────────── Recent payments ─────────────────────────
    private JPanel buildRecentSection() {
        JPanel wrap = roundedPanel(14);
        wrap.setLayout(new BorderLayout(0, 10));
        wrap.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Recent Payments");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(TEXT_DARK);

        String[] cols = {"Student", "Course", "Amount (₱)", "Method", "Reference", "Date"};
        recentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        recentTable = new JTable(recentModel);
        UIHelper.styleTable(recentTable);

        // Right-align cells and headers for columns: Amount (2), Method (3), Reference (4), Date (5)
        for (int col : new int[]{2, 3, 4, 5}) {
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            recentTable.getColumnModel().getColumn(col).setCellRenderer(cellRenderer);

            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
                @Override public Font getFont() { return UIHelper.BOLD_FONT; }
            };
            headerRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            headerRenderer.setBackground(UIHelper.HEADER_BG);
            headerRenderer.setForeground(UIHelper.HEADER_FG);
            headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            headerRenderer.setOpaque(true);
            recentTable.getColumnModel().getColumn(col).setHeaderRenderer(headerRenderer);
        }

        int[] widths = {160, 180, 110, 110, 160, 100};
        for (int i = 0; i < widths.length; i++)
            recentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        emptyLabel = UIHelper.createEmptyStateLabel("No payment records found.");
        JPanel tableWrapper = UIHelper.createTableWithOverlay(recentTable, emptyLabel);
        tableWrapper.setPreferredSize(new Dimension(10, 260));

        wrap.add(heading, BorderLayout.NORTH);
        wrap.add(tableWrapper, BorderLayout.CENTER);
        return wrap;
    }

    // ───────────────────────── Shared card style ─────────────────────────
    private JPanel roundedPanel(int radius) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ───────────────────────── Data loading ─────────────────────────
    public void refresh() {
        int totalStudents = controller.getTotalStudents();
        int newStudents = controller.getNewStudentsThisMonth();
        studentsValue.setText(String.valueOf(totalStudents));
        studentsSub.setText(newStudents + " new this month");

        coursesValue.setText(String.valueOf(controller.getTotalCourses()));
        coursesSub.setText("Currently active");

        int totalEnroll = controller.getTotalEnrollments();
        int newEnroll = controller.getNewEnrollmentsThisMonth();
        enrollmentsValue.setText(String.valueOf(totalEnroll));
        enrollmentsSub.setText(newEnroll + " new this month");

        double totalRev = controller.getTotalRevenue();
        double revThisMonth = controller.getRevenueThisMonth();
        revenueValue.setText(String.format("₱%,.0f", totalRev));
        revenueSub.setText(String.format("₱%,.0f this month", revThisMonth));

        enrollmentChart.setData(controller.getEnrollmentTrends());
        revenueChart.setData(controller.getRevenueOverview());

        recentModel.setRowCount(0);
        List<Payment> payments = controller.getRecentPayments(10);
        for (Payment p : payments) {
            recentModel.addRow(new Object[]{
                p.getStudentName(), p.getCourseName(),
                String.format("%,.2f", p.getAmount()), p.getPaymentMethod(),
                p.getReferenceNumber(),
                p.getPaymentDate() != null ? p.getPaymentDate().toString() : ""
            });
        }
        UIHelper.setEmptyStateVisible(recentTable, emptyLabel);
    }
}