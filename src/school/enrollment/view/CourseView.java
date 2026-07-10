package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        split.setResizeWeight(0.35);
        split.setDividerSize(5);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Course Management"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(20);
        txtUnits = new JTextField(5);
        txtTuition = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Course Code*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtCourseCode, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Course Name*:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtCourseName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Units:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtUnits, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Tuition/Unit (P):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtTuition, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        btnAdd.addActionListener(e -> {
            try {
                int units = Integer.parseInt(txtUnits.getText().trim());
                double tuition = Double.parseDouble(txtTuition.getText().trim());
                controller.addCourse(txtCourseCode.getText(), txtCourseName.getText(), units, tuition);
                clearForm();
                controller.loadCourses(tblCourses);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Units and Tuition must be valid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedCourseId <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a course from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int units = Integer.parseInt(txtUnits.getText().trim());
                double tuition = Double.parseDouble(txtTuition.getText().trim());
                controller.updateCourse(selectedCourseId, txtCourseCode.getText(), txtCourseName.getText(), units, tuition);
                clearForm();
                controller.loadCourses(tblCourses);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Units and Tuition must be valid numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedCourseId <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a course from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.deleteCourse(selectedCourseId);
            clearForm();
            controller.loadCourses(tblCourses);
        });

        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Course List"));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> controller.searchCourses(tblCourses, txtSearch.getText()));
        txtSearch.addActionListener(e -> controller.searchCourses(tblCourses, txtSearch.getText()));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            controller.loadCourses(tblCourses);
        });
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblCourses = new JTable(new DefaultTableModel(new Object[]{"ID", "Code", "Name", "Units", "Tuition/Unit", "Total Tuition"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
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
        txtCourseCode.setText("");
        txtCourseName.setText("");
        txtUnits.setText("");
        txtTuition.setText("");
        selectedCourseId = -1;
        tblCourses.clearSelection();
    }
}
