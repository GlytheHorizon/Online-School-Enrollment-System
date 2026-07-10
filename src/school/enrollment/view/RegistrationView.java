package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import school.enrollment.controller.StudentController;
import school.enrollment.model.Student;

public class RegistrationView extends JPanel {
    private final StudentController controller;
    private JFormattedTextField txtStudentId;
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTextArea txtAddress;
    private JTable tblStudents;
    private JTextField txtSearch;
    private String selectedStudentId;

    public RegistrationView() {
        controller = new StudentController();
        selectedStudentId = null;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        split.setResizeWeight(0.38);
        split.setDividerSize(5);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Student Registration Form"));

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            MaskFormatter idMask = new MaskFormatter("####-####");
            idMask.setPlaceholderCharacter('_');
            txtStudentId = new JFormattedTextField(idMask);
            txtStudentId.setColumns(10);
        } catch (Exception ex) {
            txtStudentId = new JFormattedTextField();
            txtStudentId.setColumns(10);
        }
        txtFirstName = new JTextField(20);
        txtLastName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Student ID* (XXXX-XXXX):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtStudentId, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("First Name*:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Last Name*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtLastName, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Email*:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtPhone, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        fields.add(new JLabel("Address:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(new JScrollPane(txtAddress), gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSave = new JButton("Register");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        btnSave.addActionListener(e -> {
            controller.registerStudent(txtStudentId.getText(), txtFirstName.getText(), txtLastName.getText(),
                txtEmail.getText(), txtPhone.getText(), txtAddress.getText());
            clearForm();
            controller.loadStudents(tblStudents);
        });

        btnUpdate.addActionListener(e -> {
            if (selectedStudentId == null) {
                JOptionPane.showMessageDialog(this, "Please select a student from the table to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.updateStudent(selectedStudentId, txtFirstName.getText(), txtLastName.getText(),
                txtEmail.getText(), txtPhone.getText(), txtAddress.getText());
            clearForm();
            controller.loadStudents(tblStudents);
        });

        btnDelete.addActionListener(e -> {
            if (selectedStudentId == null) {
                JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.deleteStudent(selectedStudentId);
            clearForm();
            controller.loadStudents(tblStudents);
        });

        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnSave);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Registered Students"));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> controller.searchStudents(tblStudents, txtSearch.getText()));
        txtSearch.addActionListener(e -> controller.searchStudents(tblStudents, txtSearch.getText()));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            controller.loadStudents(tblStudents);
        });
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblStudents = new JTable(new DefaultTableModel(new Object[]{"Student ID", "First Name", "Last Name", "Email", "Phone", "Address"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        tblStudents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblStudents.getSelectedRow();
                if (row >= 0) {
                    selectedStudentId = (String) tblStudents.getValueAt(row, 0);
                    txtStudentId.setValue(selectedStudentId);
                    txtFirstName.setText((String) tblStudents.getValueAt(row, 1));
                    txtLastName.setText((String) tblStudents.getValueAt(row, 2));
                    txtEmail.setText((String) tblStudents.getValueAt(row, 3));
                    txtPhone.setText((String) tblStudents.getValueAt(row, 4));
                    txtAddress.setText((String) tblStudents.getValueAt(row, 5));
                }
            }
        });
        controller.loadStudents(tblStudents);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblStudents), BorderLayout.CENTER);
        return panel;
    }

    private void clearForm() {
        txtStudentId.setValue(null);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        selectedStudentId = null;
        tblStudents.clearSelection();
    }
}
