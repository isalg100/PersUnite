package persunite;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class AgentTableManager {

    // =====================================================
    // VARIABLES
    // =====================================================

    private JPanel AgentTable;

    private JTable table;

    private DefaultTableModel model;

    private JTextField txtRecherche;

    private JLabel lblSelectedAgent;

    private JLabel lblSelectedGroup;
    
    private TableRowSorter<DefaultTableModel> sorter;
    private String selectedMatrim;
    private String selectedGroupId;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AgentTableManager(JPanel AgentTable) {

        this.AgentTable = AgentTable;

        initTable();

        loadAgents();
    }

    // =====================================================
    // INIT TABLE
    // =====================================================

    private void initTable() {

       
        
        AgentTable.removeAll();

        AgentTable.setLayout(
            new BorderLayout(10, 10)
        );

        AgentTable.setBackground(
            new Color(245, 247, 250)
        );

        // =================================================
        // TOP PANEL
        // =================================================

        JPanel topPanel =
            new JPanel(
                new BorderLayout(10, 10)
            );

        topPanel.setBackground(Color.WHITE);

        topPanel.setBorder(
            new EmptyBorder(10, 10, 10, 10)
        );

        JLabel title =
            new JLabel("Gestion des Agents");

        title.setFont(
            new Font(
                "Segoe UI",
                Font.BOLD,
                20
            )
        );

        txtRecherche =
            new JTextField();

        txtRecherche.setPreferredSize(
            new Dimension(250, 35)
        );

        txtRecherche.setFont(
            new Font(
                "Segoe UI",
                Font.PLAIN,
                14
            )
        );

        JPanel searchPanel =
            new JPanel(
                new FlowLayout(
                    FlowLayout.RIGHT
                )
            );

        searchPanel.setOpaque(false);

        searchPanel.add(
            new JLabel("Recherche : ")
        );

        searchPanel.add(txtRecherche);

        topPanel.add(
            title,
            BorderLayout.WEST
        );

        topPanel.add(
            searchPanel,
            BorderLayout.EAST
        );

        // =================================================
        // TABLE MODEL
        // =================================================

        model =
            new DefaultTableModel() {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {

                return false;
            }
        };

        model.setColumnIdentifiers(
            new Object[]{

                "MATRIM",
                "Nom",
                "Prénom",
                "Fonction",
                "Group",
                "Statut"
            }
        );

        // =================================================
        // TABLE
        // =================================================

        table =
            new JTable(model);
        sorter =
    new TableRowSorter<>(model);

table.setRowSorter(sorter);

table.setRowHeight(34);

        table.setRowHeight(34);

        table.setFont(
            new Font(
                "Segoe UI",
                Font.PLAIN,
                14
            )
        );

        table.setSelectionBackground(
            new Color(0, 120, 215)
        );

        table.setSelectionForeground(
            Color.WHITE
        );

        table.setGridColor(
            new Color(230, 230, 230)
        );

        table.setShowGrid(true);

        table.setIntercellSpacing(
            new Dimension(0, 1)
        );

        table.setFillsViewportHeight(true);

        table.setAutoResizeMode(
            JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        // =================================================
        // HEADER STYLE
        // =================================================

        JTableHeader header =
            table.getTableHeader();

        header.setFont(
            new Font(
                "Segoe UI",
                Font.BOLD,
                14
            )
        );

        header.setBackground(
            new Color(45, 62, 80)
        );

        header.setForeground(
            Color.WHITE
        );

        header.setPreferredSize(
            new Dimension(
                header.getWidth(),
                38
            )
        );

        // =================================================
        // COLUMN WIDTHS
        // =================================================

        TableColumn colMat =
            table.getColumnModel().getColumn(0);

        colMat.setPreferredWidth(90);

        TableColumn colNom =
            table.getColumnModel().getColumn(1);

        colNom.setPreferredWidth(180);

        TableColumn colPrenom =
            table.getColumnModel().getColumn(2);

        colPrenom.setPreferredWidth(180);

        TableColumn colFonction =
            table.getColumnModel().getColumn(3);

        colFonction.setPreferredWidth(180);

        TableColumn colFouj =
            table.getColumnModel().getColumn(4);

        colFouj.setPreferredWidth(120);

        TableColumn colStatut =
            table.getColumnModel().getColumn(5);

        colStatut.setPreferredWidth(120);

        // =================================================
        // CENTER RENDERER
        // =================================================

        DefaultTableCellRenderer center =   new DefaultTableCellRenderer();

        center.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        table.getColumnModel()
                .getColumn(0)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(5)
                .setCellRenderer(center);

        // =================================================
        // CUSTOM RENDERER
        // =================================================

        table.setDefaultRenderer(
            Object.class,
            new ModernTableRenderer()
        );

        // =================================================
        // SCROLL
        // =================================================

        JScrollPane scroll =
            new JScrollPane(table);

        scroll.setBorder(
            BorderFactory.createEmptyBorder()
        );

        // =================================================
        // DETAILS PANEL
        // =================================================

        JPanel detailsPanel =
            new JPanel();

        detailsPanel.setLayout(
            new GridLayout(2, 1, 5, 5)
        );

        detailsPanel.setBackground(
            Color.WHITE
        );

        detailsPanel.setBorder(
            new EmptyBorder(
                10,
                15,
                10,
                15
            )
        );

        lblSelectedAgent =
            new JLabel("Agent : ---");

        lblSelectedGroup =
            new JLabel("Group : ---");

        Font detailsFont =
            new Font(
                "Segoe UI",
                Font.BOLD,
                14
            );

        lblSelectedAgent.setFont(
            detailsFont
        );

        lblSelectedGroup.setFont(
            detailsFont
        );

        detailsPanel.add(
            lblSelectedAgent
        );

        detailsPanel.add(
            lblSelectedGroup
        );

        // =================================================
        // SELECTION EVENT
        // =================================================

        table.getSelectionModel()
                .addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() != -1) {

                int row =
                    table.getSelectedRow();
 
                lblSelectedAgent.setText(
  
                    "Agent : "
                    + table.getValueAt(row, 0)
                    + " "        
                    + table.getValueAt(row, 1)
                    + " "
                    + table.getValueAt(row, 2)
                );
                selectedMatrim =   (String) table.getValueAt(row, 0) ;
                lblSelectedGroup.setText(

                    "Group : "
                    + table.getValueAt(row, 4)
                );
            }
        });

        // =================================================
        // SEARCH EVENT
        // =================================================

       txtRecherche.getDocument()
        .addDocumentListener(

    new javax.swing.event.DocumentListener() {

    @Override
    public void insertUpdate(
            javax.swing.event.DocumentEvent e) {

        searchAgent();
    }

    @Override
    public void removeUpdate(
            javax.swing.event.DocumentEvent e) {

        searchAgent();
    }

    @Override
    public void changedUpdate(
            javax.swing.event.DocumentEvent e) {

        searchAgent();
    }
});

        // =================================================
        // ADD COMPONENTS
        // =================================================

        AgentTable.add(
            topPanel,
            BorderLayout.NORTH
        );

        AgentTable.add(
            scroll,
            BorderLayout.CENTER
        );

        AgentTable.add(
            detailsPanel,
            BorderLayout.SOUTH
        );

        AgentTable.revalidate();

        AgentTable.repaint();
        
    }

    // =====================================================
    // LOAD AGENTS
    // =====================================================

    private void loadAgents() {

        model.setRowCount(0);

        try (Connection conn =
                DBManager.getConnection()) {

            String sql =
                "SELECT " +

                "a.MATRIM, " +
                "a.NOMX, " +
                "a.PRENX, " +

                "IFNULL(a.LIBELLE, '') AS fonction, " +

                "IFNULL(wg.nom, '---') " +
                "AS workgroup_name, " +

                "CASE " +
                "   WHEN ea.active = 1 " +
                "   THEN '✔ ACTIF' " +
                "   ELSE '✖ INACTIF' " +
                "END AS statut " +

                "FROM agent a " +

                "LEFT JOIN EmployeeAssignment ea " +
                "ON ea.agent_matrim = a.MATRIM " +
                "AND ea.active = 1 " +

                "LEFT JOIN WorkGroup wg " +
                "ON wg.id = ea.group_id " +

                "WHERE a.AFFECX=? " +

                "ORDER BY a.NOMX";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(
                1,
                UserSession.affecx
            );

            ResultSet rs =
                ps.executeQuery();

            while (rs.next()) {

                model.addRow(
                    new Object[]{

                        rs.getString("MATRIM"),
                        rs.getString("NOMX"),
                        rs.getString("PRENX"),
                        rs.getString("fonction"),
                        rs.getString("workgroup_name"),
                        rs.getString("statut")
                    }
                );
            }

            System.out.println(
                "AGENTS LOADED = "
                + model.getRowCount()
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                null,
                ex.getMessage()
            );
        }
    }

    // =====================================================
    // SEARCH
    // =====================================================

private void searchAgent() {

    String text =
        txtRecherche.getText()
        .trim();

    if (text.isEmpty()) {

        sorter.setRowFilter(null);

    } else {

        sorter.setRowFilter(
            RowFilter.regexFilter(
                "(?i)" + text
            )
        );
    }
}

    // =====================================================
    // CUSTOM RENDERER
    // =====================================================

    public class ModernTableRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(

                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Component c =
                super.getTableCellRendererComponent(

                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
                );

            // =============================================
            // FONT
            // =============================================

            c.setFont(
                new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
                )
            );

            // =============================================
            // ALTERNATING ROWS
            // =============================================

            if (!isSelected) {

                if (row % 2 == 0) {

                    c.setBackground(
                        Color.WHITE
                    );

                } else {

                    c.setBackground(
                        new Color(245, 245, 245)
                    );
                }

                c.setForeground(
                    Color.BLACK
                );
            }

            // =============================================
            // STATUS COLORS
            // =============================================

            String statut =
                table.getValueAt(row, 5)
                .toString();

            if (!isSelected) {

                if (statut.contains("ABS")) {

                    c.setBackground(
                        new Color(255, 220, 220)
                    );

                } else if (statut.contains("RET")) {

                    c.setBackground(
                        new Color(255, 235, 200)
                    );

                } else if (statut.contains("MISSION")) {

                    c.setBackground(
                        new Color(220, 235, 255)
                    );

                } else if (statut.contains("ACTIF")) {

                    c.setBackground(
                        new Color(225, 255, 225)
                    );

                } else if (statut.contains("INACTIF")) {

                    c.setBackground(
                        new Color(235, 235, 235)
                    );
                }
            }

            // =============================================
            // PADDING
            // =============================================

            if (c instanceof JLabel lbl) {

                lbl.setBorder(
                    new EmptyBorder(
                        0,
                        8,
                        0,
                        8
                    )
                );
            }

            return c;
        }
    }
}