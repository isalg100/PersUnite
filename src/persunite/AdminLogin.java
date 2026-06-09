package persunite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class AdminLogin extends javax.swing.JDialog {

    public AdminLogin(java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        initComponents();

        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(
            javax.swing.WindowConstants.DISPOSE_ON_CLOSE
        );

        setTitle("Connexion");

        setResizable(false);

        jLabel1.setText("Nom utilisateur");

        jLabel2.setText("Mot de passe");

        jButton1.setText("Connexion");

        jButton1.addActionListener(
            new java.awt.event.ActionListener() {

                public void actionPerformed(
                        java.awt.event.ActionEvent evt) {

                    jButton1ActionPerformed(evt);
                }
            }
        );

        jButton2.setText("Annuler");

        jButton2.addActionListener(
            new java.awt.event.ActionListener() {

                public void actionPerformed(
                        java.awt.event.ActionEvent evt) {

                    jButton2ActionPerformed(evt);
                }
            }
        );

        javax.swing.GroupLayout jPanel1Layout =
            new javax.swing.GroupLayout(jPanel1);

        jPanel1.setLayout(jPanel1Layout);

        jPanel1Layout.setHorizontalGroup(

            jPanel1Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING
            )

            .addGroup(

                jPanel1Layout.createSequentialGroup()

                .addGap(25, 25, 25)

                .addGroup(

                    jPanel1Layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING
                    )

                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                )

                .addGap(18, 18, 18)

                .addGroup(

                    jPanel1Layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING,
                        false
                    )

                    .addComponent(
                        jTextField1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        250,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )

                    .addComponent(
                        jPasswordField1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        250,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )

                    .addGroup(

                        jPanel1Layout.createSequentialGroup()

                        .addComponent(
                            jButton1,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            110,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )

                        .addGap(18, 18, 18)

                        .addComponent(
                            jButton2,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            110,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                    )
                )

                .addContainerGap(25, Short.MAX_VALUE)
            )
        );

        jPanel1Layout.setVerticalGroup(

            jPanel1Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING
            )

            .addGroup(

                jPanel1Layout.createSequentialGroup()

                .addGap(30, 30, 30)

                .addGroup(

                    jPanel1Layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE
                    )

                    .addComponent(jLabel1)

                    .addComponent(
                        jTextField1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        32,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )
                )

                .addGap(20, 20, 20)

                .addGroup(

                    jPanel1Layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE
                    )

                    .addComponent(jLabel2)

                    .addComponent(
                        jPasswordField1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        32,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )
                )

                .addGap(30, 30, 30)

                .addGroup(

                    jPanel1Layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE
                    )

                    .addComponent(
                        jButton1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        35,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )

                    .addComponent(
                        jButton2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        35,
                        javax.swing.GroupLayout.PREFERRED_SIZE
                    )
                )

                .addContainerGap(30, Short.MAX_VALUE)
            )
        );

        getContentPane().add(jPanel1);

        pack();
    }
    // </editor-fold>

    // =====================================================
    // LOGIN
    // =====================================================

    private void jButton1ActionPerformed(
            java.awt.event.ActionEvent evt) {

        String username =
            jTextField1.getText().trim();

        String password =
            new String(
                jPasswordField1.getPassword()
            );

        // =========================================
        // VALIDATION
        // =========================================

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Veuillez remplir tous les champs"
            );

            return;
        }

        try (Connection conn =
                DBManager.getConnection()) {

            String sql =
                "SELECT " +
                "id, " +
                "username, " +
                "password_hash, " +
                "role, " +
                "full_name, " +
                "matrim " +
                "FROM users " +
                "WHERE username=? " +
                "AND actif=1 " +
                "AND deleted=0";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, username);

            ResultSet rs =
                ps.executeQuery();

            // =========================================
            // USER NOT FOUND
            // =========================================

            if (!rs.next()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Utilisateur introuvable"
                );

                return;
            }

            String dbPassword =
                rs.getString("password_hash");

            // =========================================
            // PASSWORD CHECK
            // =========================================

            // TEMPORAIRE
            // PLUS TARD BCrypt

            if (!password.equals(dbPassword)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Mot de passe incorrect"
                );

                return;
            }

            // =========================================
            // SESSION
            // =========================================

            UserSession.userId =
                rs.getInt("id");

            UserSession.username =
                rs.getString("username");

            UserSession.role =
                rs.getString("role");

            UserSession.fullName =
                rs.getString("full_name");

            UserSession.matrim =
                rs.getString("matrim");

            // =========================================
            // LOAD AGENT DATA
            // =========================================

            if (UserSession.matrim != null
                    && !UserSession.matrim.trim().isEmpty()) {

                String agentSql =
                    "SELECT " +
                    "NOMX, " +
                    "PRENX, " +
                    "AFFECX " +
                    "FROM agent " +
                    "WHERE MATRIM=?";

                PreparedStatement psAgent =
                    conn.prepareStatement(agentSql);

                psAgent.setString(
                    1,
                    UserSession.matrim
                );

                ResultSet rsAgent =
                    psAgent.executeQuery();

                if (rsAgent.next()) {

                    UserSession.agentNom =
                        rsAgent.getString("NOMX");

                    UserSession.agentPrenom =
                        rsAgent.getString("PRENX");

                    UserSession.affecx =
                        rsAgent.getInt("AFFECX");
                }
            }

            // =========================================
            // LOAD UNIT DATA
            // =========================================

            String uniteSql =
                "SELECT id, libelle " +
                "FROM unite " +
                "WHERE struct_id=?";

            PreparedStatement psUnite =
                conn.prepareStatement(uniteSql);

            psUnite.setInt(
                1,
                UserSession.affecx
            );

            ResultSet rsUnite =
                psUnite.executeQuery();

            if (rsUnite.next()) {

                UserSession.uniteId =
                    rsUnite.getInt("id");

                UserSession.uniteName =
                    rsUnite.getString("libelle");
            }

            // =========================================
            // DEBUG CONSOLE
            // =========================================

            System.out.println(
                "====================================="
            );

            System.out.println(
                "LOGIN SUCCESS"
            );

            System.out.println(
                "USER = "
                + UserSession.username
            );

            System.out.println(
                "ROLE = "
                + UserSession.role
            );

            System.out.println(
                "MATRIM = "
                + UserSession.matrim
            );

            System.out.println(
                "AGENT = "
                + UserSession.agentNom
                + " "
                + UserSession.agentPrenom
            );

            System.out.println(
                "UNITE = "
                + UserSession.uniteName
            );

            System.out.println(
                "====================================="
            );

            // =========================================
            // OPEN MAIN FRAME
            // =========================================

            dispose();

            PersUniteFrame frame =
                new PersUniteFrame();

            // SUPER ADMIN

            if ("SUPER_ADMIN".equals(
                    UserSession.role)) {

                frame.setAdminAccess(true);

            } else {

                frame.setAdminAccess(false);
            }

            frame.setVisible(true);

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage()
            );
        }
    }

    // =====================================================
    // CANCEL
    // =====================================================

    private void jButton2ActionPerformed(
            java.awt.event.ActionEvent evt) {

        System.exit(0);
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            AdminLogin dialog =
                new AdminLogin(
                    new javax.swing.JFrame(),
                    true
                );

            dialog.addWindowListener(

                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        System.exit(0);
                    }
                }
            );

            dialog.setVisible(true);
        });
    }

    // =====================================================
    // VARIABLES
    // =====================================================

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;

    private javax.swing.JPanel jPanel1;

    private javax.swing.JPasswordField jPasswordField1;

    private javax.swing.JTextField jTextField1;
}