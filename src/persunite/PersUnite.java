/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package persunite;

import javax.swing.JOptionPane;

/**
 *
 * @author cclsinformatique
 */
public class PersUnite {

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            try {

                // =====================================
                // INIT DATABASE
                // =====================================
                DBManager.initDatabase(null);

                // =====================================
                // ADMIN LOGIN
                // =====================================
                AdminLogin login = new AdminLogin(null, true);

                login.setVisible(true);

                // =====================================
                // SUCCESS LOGIN
                // =====================================
                

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
