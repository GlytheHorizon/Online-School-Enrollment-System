package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import school.enrollment.controller.CourseController;

public class CourseView extends JPanel {
    private final CourseController controller;
    private JTextField txtCourseCode, txtCourseName, txtUnits, txtTuition;
    private JTable tblCourses;
    private JTextField txtSearch;
    private int selectedCourseId;

    public CourseView() {
        controller = new CourseController();
        selectedCourseId = -1;
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.35);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setTopComponent(createFormPanel());
        split.setBottomComponent(createTablePanel());
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIHelper.createBorder("Course Management"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridBagLayout());
        UIHelper.stylePanel(fields);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(20);
        txtUnits = new JTextField(5);
        txtTuition = new JTextField(10);
        for (JTextField f : new JTextField[]{txtCourseCode, txtCourseName, txtUnits, txtTuition})
            UIHelper.styleField(f);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l1 = new JLabel("Course Code*:");
        UIHelper.styleLabel(l1); fields.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtCourseCode, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l2 = new JLabel("Course Name*:");
        UIHelper.styleLabel(l2); fields.add(l2, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtCourseName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l3 = new JLabel("Units:");
        UIHelper.styleLabel(l3); fields.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtUnits, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l4 = new JLabel("Tuition/Unit (P):");
        UIHelper.styleLabel(l4); fields.add(l4, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtTuition, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnAdd = UIHelper.createButton("Add", UIHelper.SUCCESS);
        JButton btnUpdate = UIHelper.createButton("Update", UIHelper.ACCENT);
        JButton btnDelete = UIHelper.createButton("Delete", new Color(192, 57, 43));
        JButton btnClear = UIHelper.createButton("Clear", new Color(149, 165, 166));

        btnAdd.addActionListener(e -> {
            try {
                controller.addCourse(txtCourseCode.getText(), txtCourseName.getText(),
                    Integer.parseInt(txtUnits.getText().trim()), Double.parseDouble(txtTuition.getText().trim()));
                clearForm(); controller.loadCourses(tblCourses);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Units and Tuition must be valid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnUpdate.addActionListener(e -> {
            if (selectedCourseId <= 0) {
                JOptionPane.showMessageDialog(this, "Select a course to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                controller.updateCourse(selectedCourseId, txtCourseCode.getText(), txtCourseName.getText(),
                    Integer.parseInt(txtUnits.getText().trim()), Double.parseDouble(txtTuition.getText().trim()));
                clearForm(); controller.loadCourses(tblCourses);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> {
            if (selectedCourseId <= 0) {
                JOptionPane.showMessageDialog(this, "Select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.deleteCourse(selectedCourseId);
            clearForm(); controller.loadCourses(tblCourses);
        });
        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete); buttons.add(btnClear);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Course List"));
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
        JButton btnRefresh = UIHelper.createButton("Refresh", UIHelper.ACCENT);
        btnSearch.addActionListener(e -> controller.searchCourses(tblCourses, txtSearch.getText()));
        txtSearch.addActionListener(e -> controller.searchCourses(tblCourses, txtSearch.getText()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); controller.loadCourses(tblCourses); });
        btnPanel.add(btnSearch); btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblCourses = new JTable(new DefaultTableModel(new Object[]{"ID", "Code", "Name", "Units", "Tuition/Unit", "Total Tuition"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblCourses);
        tblCourses.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblCourses.getSelectedRow();
                if (row >= 0) {
                    selectedCourseId = (int) tblCourses.getValueAt(row, 0);
                    txtCourseCode.setText((String) tblCourses.getValueAt(row, 1));
                    txtCourseName.setText((String) tblCourses.getValueAt(row, 2));
                    txtUnits.setText(tblCourses.getValueAt(row, 3).toString());
                    txtTuition.setText(tblCourses.getValueAt(row, 4).toString().replace(",", ""));
                }
            }
        });
        controller.loadCourses(tblCourses);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblCourses), BorderLayout.CENTER);
        return panel;
    }

    private void clearForm() {
        txtCourseCode.setText(""); txtCourseName.setText(""); txtUnits.setText(""); txtTuition.setText("");
        selectedCourseId = -1;
        tblCourses.clearSelection();
    }
}
