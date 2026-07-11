package school.enrollment.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import school.enrollment.controller.StudentController;

public class RegistrationView extends JPanel {
    private final StudentController controller;
    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTextArea txtAddress;
    private JTable tblStudents;
    private JTextField txtSearch;
    private String selectedStudentId;

    public RegistrationView() {
        controller = new StudentController();
        selectedStudentId = null;
        UIHelper.stylePanel(this);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.38);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setTopComponent(createFormPanel());
        split.setBottomComponent(createTablePanel());
        add(split, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIHelper.createBorder("Student Registration Form"));
        UIHelper.stylePanel(panel);

        JPanel fields = new JPanel(new GridBagLayout());
        UIHelper.stylePanel(fields);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(10);
        txtFirstName = new JTextField(20);
        txtLastName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        for (JTextField f : new JTextField[]{txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhone})
            UIHelper.styleField(f);
        ((PlainDocument) txtPhone.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 11) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 11) return;
                fb.replace(0, cur.length(), ns, a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                fb.replace(0, cur.length(), ns, null);
            }
        });
        txtAddress.setFont(UIHelper.MAIN_FONT);

        ((PlainDocument) txtStudentId.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), a);
            }
            public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a) throws BadLocationException {
                if (str == null) { fb.replace(offs, len, null, a); return; }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + str + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                if (ns.length() > 8) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), a);
            }
            public void remove(FilterBypass fb, int offs, int len) throws BadLocationException {
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String ns = (cur.substring(0, offs) + cur.substring(offs + len)).replaceAll("[^\\d]", "");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ns.length(); i++) { if (i == 4) sb.append('-'); sb.append(ns.charAt(i)); }
                fb.replace(0, cur.length(), sb.toString(), null);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l1 = new JLabel("Student ID* (XXXX-XXXX):");
        UIHelper.styleLabel(l1); fields.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtStudentId, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l2 = new JLabel("First Name*:");
        UIHelper.styleLabel(l2); fields.add(l2, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l3 = new JLabel("Last Name*:");
        UIHelper.styleLabel(l3); fields.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtLastName, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l4 = new JLabel("Email*:");
        UIHelper.styleLabel(l4); fields.add(l4, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel l5 = new JLabel("Phone:");
        UIHelper.styleLabel(l5); fields.add(l5, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fields.add(txtPhone, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHEAST;
        JLabel l6 = new JLabel("Address:");
        UIHelper.styleLabel(l6); fields.add(l6, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        fields.add(new JScrollPane(txtAddress), gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIHelper.stylePanel(buttons);
        JButton btnSave = UIHelper.createButton("Register", UIHelper.SUCCESS);
        JButton btnUpdate = UIHelper.createButton("Update", UIHelper.ACCENT);
        JButton btnDelete = UIHelper.createButton("Delete", new Color(192, 57, 43));
        JButton btnClear = UIHelper.createButton("Clear", new Color(149, 165, 166));

        btnSave.addActionListener(e -> {
            controller.registerStudent(txtStudentId.getText(), txtFirstName.getText(), txtLastName.getText(),
                txtEmail.getText(), txtPhone.getText(), txtAddress.getText());
            clearForm(); controller.loadStudents(tblStudents);
        });
        btnUpdate.addActionListener(e -> {
            if (selectedStudentId == null) {
                JOptionPane.showMessageDialog(this, "Select a student to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.updateStudent(selectedStudentId, txtFirstName.getText(), txtLastName.getText(),
                txtEmail.getText(), txtPhone.getText(), txtAddress.getText());
            clearForm(); controller.loadStudents(tblStudents);
        });
        btnDelete.addActionListener(e -> {
            if (selectedStudentId == null) {
                JOptionPane.showMessageDialog(this, "Select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.deleteStudent(selectedStudentId);
            clearForm(); controller.loadStudents(tblStudents);
        });
        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnSave); buttons.add(btnUpdate); buttons.add(btnDelete); buttons.add(btnClear);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(UIHelper.createBorder("Registered Students"));
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
        btnSearch.addActionListener(e -> controller.searchStudents(tblStudents, txtSearch.getText()));
        txtSearch.addActionListener(e -> controller.searchStudents(tblStudents, txtSearch.getText()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); controller.loadStudents(tblStudents); });
        btnPanel.add(btnSearch); btnPanel.add(btnRefresh);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        tblStudents = new JTable(new DefaultTableModel(new Object[]{"Student ID", "First Name", "Last Name", "Email", "Phone", "Address"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        });
        UIHelper.styleTable(tblStudents);
        tblStudents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblStudents.getSelectedRow();
                if (row >= 0) {
                    selectedStudentId = (String) tblStudents.getValueAt(row, 0);
                    txtStudentId.setText(selectedStudentId);
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
        txtStudentId.setText("");
        txtFirstName.setText(""); txtLastName.setText(""); txtEmail.setText("");
        txtPhone.setText(""); txtAddress.setText("");
        selectedStudentId = null;
        tblStudents.clearSelection();
    }
}
