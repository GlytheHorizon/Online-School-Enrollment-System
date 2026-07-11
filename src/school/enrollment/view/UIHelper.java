package school.enrollment.view;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.*;

public class UIHelper {
    public static final Color HEADER_BG = new Color(52, 73, 94);
    public static final Color HEADER_FG = Color.BLACK;
    public static final Color PANEL_BG = new Color(245, 246, 250);
    public static final Color FIELD_BG = Color.WHITE;
    public static final Color LABEL_FG = Color.BLACK;
    public static final Color ACCENT = new Color(41, 128, 185);
    public static final Color SUCCESS = new Color(39, 174, 96);
    public static final Color WARNING = new Color(243, 156, 18);
    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(200, 210, 230));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(BOLD_FONT);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(HEADER_FG);
        table.getTableHeader().setReorderingAllowed(false);
        ((JComponent) table.getTableHeader()).setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, HEADER_BG));
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static TitledBorder createBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            title, TitledBorder.LEADING, TitledBorder.TOP,
            BOLD_FONT, Color.BLACK);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
    }

    public static void styleField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setBackground(FIELD_BG);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(MAIN_FONT);
        label.setForeground(LABEL_FG);
    }

    public static void setPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (placeholder.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        if (field.getText().isEmpty()) {
            field.setText(placeholder);
            field.setForeground(Color.GRAY);
        }
    }

    public static void setPlaceholder(JTextArea area, String placeholder) {
        area.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (placeholder.equals(area.getText())) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(Color.GRAY);
                }
            }
        });
        if (area.getText().isEmpty()) {
            area.setText(placeholder);
            area.setForeground(Color.GRAY);
        }
    }
}
