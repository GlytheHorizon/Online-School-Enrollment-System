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
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

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

        JPanel fields = new JPanel(new GridLayout(0, 2, 18, 12));
        UIHelper.stylePanel(fields);

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(20);
        txtUnits = new JTextField(5);
        txtTuition = new JTextField(10);
        for (JTextField f : new JTextField[]{txtCourseCode, txtCourseName, txtUnits, txtTuition})
            UIHelper.styleField(f);

        fields.add(UIHelper.createLabeledField("Course Code*", txtCourseCode));
        fields.add(UIHelper.createLabeledField("Course Name*", txtCourseName));
        fields.add(UIHelper.createLabeledField("Units", txtUnits));
        fields.add(UIHelper.createLabeledField("Tuition/Unit (P)", txtTuition));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        UIHelper.stylePanel(buttons);
        btnAdd = UIHelper.createButton("Add", UIHelper.SUCCESS);
        btnUpdate = UIHelper.createButton("Update", UIHelper.ACCENT);
        btnDelete = UIHelper.createButton("Delete", new Color(192, 57, 43));
        btnClear = UIHelper.createGhostButton("Clear");

        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);

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
        
        JPanel fieldsWrapper = new JPanel(new BorderLayout());
        UIHelper.stylePanel(fieldsWrapper);
        fieldsWrapper.add(fields, BorderLayout.NORTH);
        
        panel.add(fieldsWrapper, BorderLayout.CENTER);
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
        UIHelper.setPlaceholder(txtSearch, "Search courses...");
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        UIHelper.stylePanel(btnPanel);
        JButton btnSearch = UIHelper.createSecondaryButton("Search");
        JButton btnRefresh = UIHelper.createGhostButton("Refresh");
        tblCourses = new JTable(new DefaultTableModel(new Object[]{"ID", "Code", "Name", "Units", "Tuition/Unit", "Total Tuition"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblCourses);
        JLabel emptyCourses = UIHelper.createEmptyStateLabel("No courses found.");
        txtSearch.addActionListener(e -> {
            controller.searchCourses(tblCourses, UIHelper.getCleanText(txtSearch));
            UIHelper.setEmptyStateVisible(tblCourses, emptyCourses);
        });
        btnSearch.addActionListener(e -> {
            controller.searchCourses(tblCourses, UIHelper.getCleanText(txtSearch));
            UIHelper.setEmptyStateVisible(tblCourses, emptyCourses);
        });
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); controller.loadCourses(tblCourses); UIHelper.setEmptyStateVisible(tblCourses, emptyCourses); });
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);
        tblCourses.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblCourses.getSelectedRow();
                if (row >= 0) {
                    selectedCourseId = (int) tblCourses.getValueAt(row, 0);
                    txtCourseCode.setText((String) tblCourses.getValueAt(row, 1));
                    txtCourseName.setText((String) tblCourses.getValueAt(row, 2));
                    txtUnits.setText(tblCourses.getValueAt(row, 3).toString());
                    txtTuition.setText(tblCourses.getValueAt(row, 4).toString().replace(",", ""));

                    btnAdd.setVisible(false);
                    btnUpdate.setVisible(true);
                    btnDelete.setVisible(true);
                }
            }
        });
        controller.loadCourses(tblCourses);
        UIHelper.setEmptyStateVisible(tblCourses, emptyCourses);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(UIHelper.createTableWithOverlay(tblCourses, emptyCourses), BorderLayout.CENTER);
        return panel;
    }

    private void clearForm() {
        txtCourseCode.setText(""); txtCourseName.setText(""); txtUnits.setText(""); txtTuition.setText("");
        selectedCourseId = -1;
        tblCourses.clearSelection();

        btnAdd.setVisible(true);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
    }
}
