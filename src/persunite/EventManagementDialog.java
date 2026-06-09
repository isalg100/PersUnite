package persunite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventManagementDialog extends JDialog {

    // =====================================================
    // VARIABLES
    // =====================================================

    private String agentMatrim;
    private String agentName;
    private int agentUniteId;

    private JComboBox<String> jComboBoxEventType;
    private JTextField jTextFieldValue;
    private JButton jButtonDateStart;
    private JButton jButtonDateEnd;
    private JTextArea jTextAreaRemarks;
    private JCheckBox jCheckBoxJustified;
    private JButton jButtonSave;
    private JButton jButtonDelete;
    private JButton jButtonCancel;
    private JTable jTableEvents;
    private DefaultTableModel tableModel;
    
    private Date selectedStartDate;
    private Date selectedEndDate;
    private int selectedEventId = -1;

    private final SimpleDateFormat sdf = 
        new SimpleDateFormat("yyyy-MM-dd");

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public EventManagementDialog(
            java.awt.Frame parent,
            String agentMatrim,
            String agentName,
            int agentUniteId
    ) {
        super(parent, "Gestion Événements - " + agentName, true);

        this.agentMatrim = agentMatrim;
        this.agentName = agentName;
        this.agentUniteId = agentUniteId;

        initComponents();
        loadEvents();
        
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }

    // =====================================================
    // INIT GUI
    // =====================================================

    private void initComponents() {

        // =================================================
        // EVENT TYPES
        // =================================================

        String[] eventTypes = {
            "--- Sélectionner un événement ---",
            "ABSENCE",
            "RETARD",
            "SORTIE_ANTICIPEE",
            "HEURES_SUPP",
            "DEPLACEMENT",
            "CONGE",
            "CONGE_MALADIE",
            "REPOS_HEBDO",
            "JOUR_FERIE",
            "ACCIDENT_TRAVAIL",
            "FORMATION",
            "MISSION",
            "SUSPENSION",
            "REPOS_MEDICAL",
            "MATERNITE",
            "PERMISSION",
            "AVERTISSEMENT",
            "AMENDE"
        };

        // =================================================
        // TOP PANEL - INPUT FORM
        // =================================================

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Agent Info
        JLabel lblAgent = new JLabel("Agent: " + agentName + " (" + agentMatrim + ")");
        lblAgent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        topPanel.add(lblAgent, gbc);

        // Event Type
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Type d'événement:"), gbc);
        
        jComboBoxEventType = new JComboBox<>(eventTypes);
        jComboBoxEventType.setPreferredSize(new Dimension(200, 35));
        jComboBoxEventType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        topPanel.add(jComboBoxEventType, gbc);

        // Value (for numeric events)
        gbc.gridx = 2;
        topPanel.add(new JLabel("Valeur:"), gbc);
        
        jTextFieldValue = new JTextField();
        jTextFieldValue.setPreferredSize(new Dimension(150, 35));
        jTextFieldValue.setToolTip("Minutes de retard, heures supp., etc.");
        gbc.gridx = 3;
        topPanel.add(jTextFieldValue, gbc);

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("Date début:"), gbc);
        
        jButtonDateStart = new JButton("Sélectionner date");
        jButtonDateStart.setPreferredSize(new Dimension(150, 35));
        jButtonDateStart.addActionListener(e -> selectStartDate());
        gbc.gridx = 1;
        topPanel.add(jButtonDateStart, gbc);

        // End Date
        gbc.gridx = 2;
        topPanel.add(new JLabel("Date fin:"), gbc);
        
        jButtonDateEnd = new JButton("Sélectionner date");
        jButtonDateEnd.setPreferredSize(new Dimension(150, 35));
        jButtonDateEnd.addActionListener(e -> selectEndDate());
        gbc.gridx = 3;
        topPanel.add(jButtonDateEnd, gbc);

        // Justified Checkbox
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        jCheckBoxJustified = new JCheckBox("Événement justifié");
        jCheckBoxJustified.setBackground(Color.WHITE);
        topPanel.add(jCheckBoxJustified, gbc);

        // Remarks
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        topPanel.add(new JLabel("Remarques:"), gbc);
        
        gbc.gridy = 5;
        gbc.weighty = 0.15;
        jTextAreaRemarks = new JTextArea(3, 50);
        jTextAreaRemarks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTextAreaRemarks.setLineWrap(true);
        jTextAreaRemarks.setWrapStyleWord(true);
        JScrollPane scrollRemarks = new JScrollPane(jTextAreaRemarks);
        topPanel.add(scrollRemarks, gbc);

        // =================================================
        // CENTER PANEL - TABLE
        // =================================================

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTable = new JLabel("Événements enregistrés:");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 12));
        centerPanel.add(lblTable, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.setColumnIdentifiers(new Object[]{
            "ID", "Type", "Date début", "Date fin", 
            "Valeur", "Justifié", "Remarques"
        });

        jTableEvents = new JTable(tableModel);
        jTableEvents.setRowHeight(25);
        jTableEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableEvents.getSelectionModel()
            .addListSelectionListener(e -> selectEventFromTable());

        JScrollPane scrollTable = new JScrollPane(jTableEvents);
        centerPanel.add(scrollTable, BorderLayout.CENTER);

        // =================================================
        // BOTTOM PANEL - BUTTONS
        // =================================================

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        jButtonSave = new JButton("Enregistrer");
        jButtonSave.setPreferredSize(new Dimension(120, 35));
        jButtonSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jButtonSave.setBackground(new Color(76, 175, 80));
        jButtonSave.setForeground(Color.WHITE);
        jButtonSave.addActionListener(e -> saveEvent());
        buttonPanel.add(jButtonSave);

        jButtonDelete = new JButton("Supprimer");
        jButtonDelete.setPreferredSize(new Dimension(120, 35));
        jButtonDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jButtonDelete.setBackground(new Color(244, 67, 54));
        jButtonDelete.setForeground(Color.WHITE);
        jButtonDelete.addActionListener(e -> deleteEvent());
        jButtonDelete.setEnabled(false);
        buttonPanel.add(jButtonDelete);

        jButtonCancel = new JButton("Fermer");
        jButtonCancel.setPreferredSize(new Dimension(120, 35));
        jButtonCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jButtonCancel.addActionListener(e -> dispose());
        buttonPanel.add(jButtonCancel);

        // =================================================
        // LAYOUT
        // =================================================

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    // =====================================================
    // SELECT DATE
    // =====================================================

    private void selectStartDate() {
        JDateChooser dateChooser = new JDateChooser();
        int option = JOptionPane.showConfirmDialog(
            this,
            dateChooser,
            "Sélectionner date début",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            selectedStartDate = dateChooser.getDate();
            if (selectedStartDate != null) {
                jButtonDateStart.setText(sdf.format(selectedStartDate));
            }
        }
    }

    private void selectEndDate() {
        JDateChooser dateChooser = new JDateChooser();
        int option = JOptionPane.showConfirmDialog(
            this,
            dateChooser,
            "Sélectionner date fin",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            selectedEndDate = dateChooser.getDate();
            if (selectedEndDate != null) {
                jButtonDateEnd.setText(sdf.format(selectedEndDate));
            }
        }
    }

    // =====================================================
    // LOAD EVENTS FROM DATABASE
    // =====================================================

    private void loadEvents() {
        tableModel.setRowCount(0);

        try (Connection conn = DBManager.getConnection()) {

            String sql = "SELECT " +
                "id, event_type, event_date, value, " +
                "justified, remarque " +
                "FROM EventLog " +
                "WHERE agent_id = ? " +
                "AND deleted = 0 " +
                "ORDER BY event_date DESC " +
                "LIMIT 50";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, agentMatrim);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("event_type"),
                    rs.getString("event_date"),
                    "",  // End date (if multi-day)
                    rs.getString("value"),
                    rs.getInt("justified") == 1 ? "✓" : "✗",
                    rs.getString("remarque")
                });
            }

            System.out.println("✅ EVENTS LOADED: " + tableModel.getRowCount());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Erreur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // SELECT EVENT FROM TABLE
    // =====================================================

    private void selectEventFromTable() {
        int row = jTableEvents.getSelectedRow();

        if (row >= 0) {
            selectedEventId = (int) tableModel.getValueAt(row, 0);
            jButtonDelete.setEnabled(true);

            // Load event details
            try (Connection conn = DBManager.getConnection()) {
                String sql = "SELECT event_type, event_date, value, " +
                    "justified, remarque FROM EventLog WHERE id = ?";
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedEventId);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    jComboBoxEventType.setSelectedItem(rs.getString("event_type"));
                    jTextFieldValue.setText(rs.getString("value") != null ? 
                        rs.getString("value") : "");
                    jTextAreaRemarks.setText(rs.getString("remarque") != null ? 
                        rs.getString("remarque") : "");
                    jCheckBoxJustified.setSelected(rs.getInt("justified") == 1);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            selectedEventId = -1;
            jButtonDelete.setEnabled(false);
        }
    }

    // =====================================================
    // SAVE EVENT
    // =====================================================

    private void saveEvent() {
        // Validation
        if (jComboBoxEventType.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez sélectionner un type d'événement",
                "Validation",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (selectedStartDate == null) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez sélectionner une date",
                "Validation",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String eventType = (String) jComboBoxEventType.getSelectedItem();
        String value = jTextFieldValue.getText().trim();
        String remarks = jTextAreaRemarks.getText().trim();
        int justified = jCheckBoxJustified.isSelected() ? 1 : 0;

        try (Connection conn = DBManager.getConnection()) {

            String sql = "INSERT INTO EventLog (" +
                "agent_id, event_type, event_date, value, " +
                "justified, remarque, source, created_at, uuid" +
                ") VALUES (?, ?, ?, ?, ?, ?, 'UNIT_APP', datetime('now'), " +
                "lower(hex(randomblob(16))))";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, agentMatrim);
            ps.setString(2, eventType);
            ps.setString(3, sdf.format(selectedStartDate));
            ps.setString(4, value.isEmpty() ? null : value);
            ps.setInt(5, justified);
            ps.setString(6, remarks);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Événement enregistré avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadEvents();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Erreur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // DELETE EVENT
    // =====================================================

    private void deleteEvent() {
        if (selectedEventId < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez sélectionner un événement à supprimer",
                "Sélection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer cet événement?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DBManager.getConnection()) {

            String sql = "UPDATE EventLog SET deleted = 1 WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedEventId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Événement supprimé",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadEvents();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Erreur: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // CLEAR FORM
    // =====================================================

    private void clearForm() {
        jComboBoxEventType.setSelectedIndex(0);
        jTextFieldValue.setText("");
        jTextAreaRemarks.setText("");
        jCheckBoxJustified.setSelected(false);
        jButtonDateStart.setText("Sélectionner date");
        jButtonDateEnd.setText("Sélectionner date");
        selectedStartDate = null;
        selectedEndDate = null;
        selectedEventId = -1;
        jButtonDelete.setEnabled(false);
    }
}
