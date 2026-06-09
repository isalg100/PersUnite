package persunite;

import java.sql.*;
import javax.swing.*;

public class ParametrageUniteDialog extends JDialog {

    public ParametrageUniteDialog(java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        initComponents();

        loadUnites();

        jButtonSave.addActionListener(e -> {
            saveParametrage();
        });

        jButtonCancel.addActionListener(e -> {
            dispose();
        });
    }

    // =====================================================
    // LOAD UNITES
    // =====================================================

    private void loadUnites() {

        try (Connection conn = DBManager.getConnection()) {

            jComboBoxUnite.removeAllItems();

            String sql =
                "SELECT id, libelle " +
                "FROM unite " +
                "ORDER BY libelle";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ComboItem item = new ComboItem(
                    rs.getInt("id"),
                    rs.getString("libelle")
                );

                jComboBoxUnite.addItem(item);
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage()
            );
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    private void saveParametrage() {

        ComboItem unite =
            (ComboItem) jComboBoxUnite.getSelectedItem();

        String matrim =
            jTextFieldMatricule.getText().trim();

        String username =
            jTextFieldUsername.getText().trim();

        String password =
            new String(
                jPasswordFieldPassword.getPassword()
            );

        String confirmPassword =
            new String(
                jPasswordFieldConfirm.getPassword()
            );

        // =========================================
        // VALIDATION
        // =========================================

        if (unite == null
                || matrim.isEmpty()
                || username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Veuillez remplir tous les champs"
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this,
                "Confirmation mot de passe incorrecte"
            );

            return;
        }

        try (Connection conn = DBManager.getConnection()) {

            conn.setAutoCommit(false);

            // =========================================
            // SAVE APP CONFIG
            // =========================================

            saveConfig(conn,
                    "UNIT_ID",
                    String.valueOf(unite.getId()));

            saveConfig(conn,
                    "UNIT_NAME",
                    unite.toString());

            saveConfig(conn,
                    "CHEF_MATRICULE",
                    matrim);

            saveConfig(conn,
                    "SYSTEM_INITIALIZED",
                    "1");

            // =========================================
            // CHECK USER EXISTS
            // =========================================

            String checkSql =
                "SELECT id FROM users " +
                "WHERE username=? " +
                "AND deleted=0";

            PreparedStatement checkPs =
                conn.prepareStatement(checkSql);

            checkPs.setString(1, username);

            ResultSet rs =
                checkPs.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Nom utilisateur existe déjà"
                );

                return;
            }

            // =========================================
            // CREATE USER
            // =========================================

            String insertUser =
    "INSERT INTO users (" +
    "uuid," +
    "username," +
    "password_hash," +
    "full_name," +
    "matrim," +
    "role," +
    "actif" +
    ") VALUES (" +
    "lower(hex(randomblob(16)))," +
    "?,?,?,?,?,1" +
    ")";

PreparedStatement ps =
    conn.prepareStatement(insertUser);

ps.setString(1, username);

ps.setString(2, password);

// full_name
ps.setString(3, username);

// matrim
ps.setString(4, matrim);

// role
ps.setString(5, "UNIT_USER");

ps.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(
                this,
                "Paramétrage enregistré avec succès"
            );

            dispose();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage()
            );
        }
    }

    // =====================================================
    // SAVE CONFIG
    // =====================================================

    private void saveConfig(
            Connection conn,
            String key,
            String value
    ) throws Exception {

        String sql =
            "INSERT OR REPLACE INTO AppConfig " +
            "(key, value) VALUES (?, ?)";

        PreparedStatement ps =
            conn.prepareStatement(sql);

        ps.setString(1, key);

        ps.setString(2, value);

        ps.executeUpdate();
    }

    // =====================================================
    // COMBO ITEM
    // =====================================================

    public static class ComboItem {

        private int id;

        private String label;

        public ComboItem(
                int id,
                String label
        ) {

            this.id = id;

            this.label = label;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // =====================================================
    // GUI
    // =====================================================

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jComboBoxUnite = new JComboBox<>();

        jTextFieldMatricule = new JTextField();

        jTextFieldUsername = new JTextField();

        jPasswordFieldPassword =
            new JPasswordField();

        jPasswordFieldConfirm =
            new JPasswordField();

        jButtonSave = new JButton();

        jButtonCancel = new JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setTitle("Paramétrage unité");

        jButtonSave.setText("Enregistrer");

        jButtonCancel.setText("Annuler");

        JLabel l1 =
            new JLabel("Unité");

        JLabel l2 =
            new JLabel("Matricule responsable");

        JLabel l3 =
            new JLabel("Nom utilisateur");

        JLabel l4 =
            new JLabel("Mot de passe");

        JLabel l5 =
            new JLabel("Confirmation mot de passe");

        GroupLayout layout =
            new GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);

        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(l1)
                .addComponent(jComboBoxUnite)

                .addComponent(l2)
                .addComponent(jTextFieldMatricule)

                .addComponent(l3)
                .addComponent(jTextFieldUsername)

                .addComponent(l4)
                .addComponent(jPasswordFieldPassword)

                .addComponent(l5)
                .addComponent(jPasswordFieldConfirm)

                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(jButtonSave)
                        .addComponent(jButtonCancel)
                )
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()

                .addComponent(l1)
                .addComponent(jComboBoxUnite)

                .addComponent(l2)
                .addComponent(jTextFieldMatricule)

                .addComponent(l3)
                .addComponent(jTextFieldUsername)

                .addComponent(l4)
                .addComponent(jPasswordFieldPassword)

                .addComponent(l5)
                .addComponent(jPasswordFieldConfirm)

                .addGroup(
                    layout.createParallelGroup()
                        .addComponent(jButtonSave)
                        .addComponent(jButtonCancel)
                )
        );

        pack();

        setLocationRelativeTo(null);
    }

    // =====================================================
    // VARIABLES
    // =====================================================

    private JComboBox<ComboItem> jComboBoxUnite;

    private JTextField jTextFieldMatricule;

    private JTextField jTextFieldUsername;

    private JPasswordField jPasswordFieldPassword;

    private JPasswordField jPasswordFieldConfirm;

    private JButton jButtonSave;

    private JButton jButtonCancel;
}