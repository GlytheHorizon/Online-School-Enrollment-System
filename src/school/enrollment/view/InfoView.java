package school.enrollment.view;

import java.awt.*;
import javax.swing.*;


public class InfoView extends JPanel {
    public InfoView() {
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        initComponents();
    }

    private void initComponents() {
        JPanel center = new JPanel(new GridBagLayout());
        UIHelper.stylePanel(center);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Online School Enrollment System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIHelper.HEADER_BG);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(title, gbc);

        JLabel sub = new JLabel("MVC + DAO Architecture  |  Java Swing + MySQL");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(100, 100, 100));
        gbc.gridy = 1;
        center.add(sub, gbc);

        JSeparator sep = new JSeparator();
        gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        center.add(sep, gbc);

        JLabel groupLabel = new JLabel("Group 2");
        groupLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        groupLabel.setForeground(UIHelper.HEADER_BG);
        gbc.gridy = 3; gbc.insets = new Insets(5, 10, 5, 10);
        center.add(groupLabel, gbc);

        String[] members = {
            "Cruz, Jerwin E.",
            "Layos, Joland",
            "Lazaro, Nathalie Jane D.",
            "Matiga, John Michael B.",
            "Villabroza, Clark Darren J."
        };

        gbc.gridwidth = 1;
        gbc.insets = new Insets(3, 10, 3, 10);
        for (int i = 0; i < members.length; i++) {
            gbc.gridy = 4 + i;
            gbc.gridx = 0;
            JLabel dot = new JLabel("  \u2022  ");
            dot.setFont(new Font("Segoe UI", Font.BOLD, 16));
            dot.setForeground(UIHelper.ACCENT);
            center.add(dot, gbc);
            gbc.gridx = 1;
            JLabel name = new JLabel(members[i]);
            name.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            name.setForeground(Color.BLACK);
            center.add(name, gbc);
        }

        gbc.gridy = 4 + members.length; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 5, 10);
        JSeparator sep2 = new JSeparator();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        center.add(sep2, gbc);

        JLabel footer = new JLabel("\u00a9 2026  |  TESDA Activity 15");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(150, 150, 150));
        gbc.gridy = 5 + members.length; gbc.anchor = GridBagConstraints.CENTER;
        center.add(footer, gbc);

        add(center, BorderLayout.CENTER);
    }
}
