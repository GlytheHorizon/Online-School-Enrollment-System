package school.enrollment.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.OverlayLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class UIHelper {
    public static final Color HEADER_BG = new Color(30, 58, 95);
    public static final Color HEADER_FG = Color.WHITE;
    public static final Color PANEL_BG = new Color(248, 250, 252);
    public static final Color FIELD_BG = Color.WHITE;
    public static final Color LABEL_FG = new Color(51, 65, 85);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color BORDER = new Color(203, 213, 225);
    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(34);
        table.setRowMargin(6);
        table.setIntercellSpacing(new Dimension(0, 4));
        table.setShowGrid(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.getTableHeader().setFont(BOLD_FONT);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(HEADER_FG);
        table.getTableHeader().setReorderingAllowed(false);
        ((JComponent) table.getTableHeader()).setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, HEADER_BG));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        final int[] hoverRow = new int[]{-1};
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoverRow[0]) {
                    hoverRow[0] = row;
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (hoverRow[0] != -1) {
                    hoverRow[0] = -1;
                    table.repaint();
                }
            }
        });

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? table.getSelectionBackground() : row == hoverRow[0] ? new Color(241, 245, 249) : table.getBackground());
                return c;
            }
        };
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        table.setDefaultRenderer(Object.class, leftRenderer);
        table.setDefaultRenderer(String.class, leftRenderer);

        table.setDefaultRenderer(Boolean.class, new DefaultTableCellRenderer() {
            private final JCheckBox checkbox = new JCheckBox();
            {
                checkbox.setHorizontalAlignment(SwingConstants.LEFT);
                checkbox.setOpaque(true);
                checkbox.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                checkbox.setSelected(Boolean.TRUE.equals(value));
                checkbox.setBackground(isSelected ? table.getSelectionBackground() : row == hoverRow[0] ? new Color(241, 245, 249) : table.getBackground());
                checkbox.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                return checkbox;
            }
        });
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(BOLD_FONT);
        Color normalBg = bg;
        Color hoverBg = bg.darker();
        Color normalFg = isLight(bg) ? Color.BLACK : Color.WHITE;
        Color hoverFg = Color.WHITE;
        btn.setBackground(normalBg);
        btn.setForeground(normalFg);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        Border normalBorder = createRoundedBorder(14, bg.darker());
        Border hoverBorder = createRoundedBorder(14, hoverBg.darker());
        btn.setBorder(normalBorder);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyHoverEffect(btn, normalBg, hoverBg, normalFg, hoverFg, normalBorder, hoverBorder);
        setButtonSize(btn);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(BOLD_FONT);
        Color normalBg = new Color(238, 242, 255);
        Color hoverBg = new Color(59, 130, 246);
        Color normalFg = ACCENT;
        Color hoverFg = Color.WHITE;
        btn.setBackground(normalBg);
        btn.setForeground(normalFg);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        Border normalBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, new Color(185, 199, 233)),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        Border hoverBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, new Color(37, 99, 235)),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setBorder(normalBorder);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyHoverEffect(btn, normalBg, hoverBg, normalFg, hoverFg, normalBorder, hoverBorder);
        setButtonSize(btn);
        return btn;
    }

    public static JButton createGhostButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(BOLD_FONT);
        Color normalBg = new Color(249, 250, 251);
        Color hoverBg = new Color(243, 244, 246);
        Color normalFg = new Color(75, 85, 99);
        Color hoverFg = new Color(31, 41, 55);
        btn.setBackground(normalBg);
        btn.setForeground(normalFg);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        Border normalBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        Border hoverBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, new Color(148, 163, 184)),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setBorder(normalBorder);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyHoverEffect(btn, normalBg, hoverBg, normalFg, hoverFg, normalBorder, hoverBorder);
        setButtonSize(btn);
        return btn;
    }

    public static JButton createOutlineButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(BOLD_FONT);
        Color normalBg = new Color(0, 0, 0, 0); // transparent background
        Color hoverBg = new Color(color.getRed(), color.getGreen(), color.getBlue(), 20); // light tint on hover
        Color normalFg = color;
        Color hoverFg = color.darker();
        btn.setBackground(normalBg);
        btn.setForeground(normalFg);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        Border normalBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, color),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        Border hoverBorder = BorderFactory.createCompoundBorder(
            createRoundedBorder(14, hoverFg),
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setBorder(normalBorder);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyHoverEffect(btn, normalBg, hoverBg, normalFg, hoverFg, normalBorder, hoverBorder);
        setButtonSize(btn);
        return btn;
    }


    private static void applyHoverEffect(JButton btn, Color normalBg, Color hoverBg, Color normalFg, Color hoverFg, Border normalBorder, Border hoverBorder) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setForeground(hoverFg);
                btn.setBorder(hoverBorder);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(normalBg);
                btn.setForeground(normalFg);
                btn.setBorder(normalBorder);
            }
        });
    }

    private static void setButtonSize(JButton btn) {
        Dimension pref = btn.getPreferredSize();
        pref.height = 38;
        pref.width = Math.max(pref.width + 20, 120);
        btn.setPreferredSize(pref);
        btn.setMaximumSize(new Dimension(320, 38));
    }

    public static JLabel createEmptyStateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        label.setForeground(new Color(107, 114, 128));
        label.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        label.setVisible(false);
        return label;
    }

    public static JPanel createTableWithOverlay(JTable table, JLabel emptyStateLabel) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new OverlayLayout(wrapper));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        emptyStateLabel.setAlignmentX(0.5f);
        emptyStateLabel.setAlignmentY(0.5f);
        wrapper.add(scrollPane);
        wrapper.add(emptyStateLabel);
        return wrapper;
    }

    public static void setEmptyStateVisible(JTable table, JLabel emptyStateLabel) {
        emptyStateLabel.setVisible(table.getRowCount() == 0);
    }

    public static TitledBorder createBorder(String title) {
        Border inner = BorderFactory.createCompoundBorder(
            createRoundedBorder(12, BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return BorderFactory.createTitledBorder(
            inner,
            title, TitledBorder.LEADING, TitledBorder.TOP,
            HEADER_FONT, HEADER_BG);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    }

    public static void styleField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(10, BORDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(MAIN_FONT);
        comboBox.setBackground(FIELD_BG);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(10, BORDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public static void styleRoundedField(JComponent component) {
        component.setOpaque(true);
        component.setBackground(FIELD_BG);
        component.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(10, BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public static void setComboPlaceholder(JComboBox<?> comboBox, String placeholder) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = (value == null || value.toString().trim().isEmpty()) ? placeholder : value.toString();
                setText(text);
                setForeground((value == null || value.toString().trim().isEmpty()) ? new Color(148, 163, 184) : LABEL_FG);
                return this;
            }
        });
    }

    public static void styleArea(JTextArea area) {
        area.setFont(MAIN_FONT);
        area.setBackground(FIELD_BG);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(10, BORDER),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(10, BORDER),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        scrollPane.setViewportBorder(null);
        scrollPane.getViewport().setBackground(FIELD_BG);
    }

    public static JPanel createLabeledField(String labelText, JComponent component) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        styleLabel(label);
        label.setFont(BOLD_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);
        // Align and enforce consistent height for a modern stacked form look
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (component instanceof javax.swing.JTextField) {
            if (!component.isPreferredSizeSet()) {
                component.setPreferredSize(new Dimension(200, 38));
            }
            if (!component.isMaximumSizeSet()) {
                component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            }
            if (!component.isMinimumSizeSet()) {
                component.setMinimumSize(new Dimension(200, 38));
            }
        } else if (component instanceof javax.swing.JComboBox) {
            if (!component.isPreferredSizeSet()) {
                component.setPreferredSize(new Dimension(200, 38));
            }
            if (!component.isMaximumSizeSet()) {
                component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            }
            if (!component.isMinimumSizeSet()) {
                component.setMinimumSize(new Dimension(200, 38));
            }
        } else if (component instanceof javax.swing.JScrollPane) {
            if (!component.isPreferredSizeSet()) {
                component.setPreferredSize(new Dimension(200, 84));
            }
            if (!component.isMaximumSizeSet()) {
                component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            }
            if (!component.isMinimumSizeSet()) {
                component.setMinimumSize(new Dimension(200, 84));
            }
        } else if (component instanceof JComponent) {
            ((JComponent) component).setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        return panel;
    }

    public static void styleLabel(JLabel label) {
        label.setFont(MAIN_FONT);
        label.setForeground(LABEL_FG);
    }

    public static JPanel createTagChip(String text, String tooltip, Runnable removeAction) {
        JPanel chip = new JPanel(new BorderLayout(4, 0));
        chip.setOpaque(true);
        chip.setBackground(new Color(249, 250, 251));
        chip.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(16, new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(6, 10, 6, 6)));

        JLabel label = new JLabel(text);
        label.setFont(MAIN_FONT.deriveFont(12f));
        label.setForeground(new Color(51, 65, 85));
        label.setToolTipText(tooltip);

        JButton removeBtn = new JButton("×");
        removeBtn.setFont(MAIN_FONT.deriveFont(Font.BOLD, 12f));
        removeBtn.setForeground(new Color(75, 85, 99));
        removeBtn.setOpaque(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> removeAction.run());

        chip.add(label, BorderLayout.CENTER);
        chip.add(removeBtn, BorderLayout.EAST);
        return chip;
    }

    public static void setPlaceholder(JTextField field, String placeholder) {
        field.putClientProperty("placeholder", placeholder);
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
        area.putClientProperty("placeholder", placeholder);
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

    public static String getCleanText(JTextComponent component) {
        Object placeholder = component.getClientProperty("placeholder");
        String text = component.getText();
        if (placeholder != null && placeholder.equals(text)) {
            return "";
        }
        return text == null ? "" : text.trim();
    }

    public static boolean isPlaceholderActive(JTextComponent component) {
        Object placeholder = component.getClientProperty("placeholder");
        return placeholder != null && placeholder.equals(component.getText());
    }

    public static Border createRoundedBorder(int radius, Color color) {
        return new RoundedBorder(radius, color);
    }

    private static boolean isLight(Color color) {
        return (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114) > 180;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 6, 4, 6);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 4;
            insets.left = 6;
            insets.bottom = 4;
            insets.right = 6;
            return insets;
        }
    }
}
