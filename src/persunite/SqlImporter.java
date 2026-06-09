/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persunite;

/**
 *
 * @author cclsinformatique
 */
import java.nio.file.*;
import java.sql.*;

public class SqlImporter {

    public static void importFile(String filePath) throws Exception {

        try (Connection conn = DBManager.getConnection()) {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] queries = content.split(";");

            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {

                for (String q : queries) {
                    q = q.trim();

                    if (q.isEmpty()) continue;

                    // 🔐 حماية بسيطة
                    if (!isAllowed(q)) {
                        throw new RuntimeException("❌ Unauthorized SQL: " + q);
                    }

                    stmt.execute(q);
                }

                conn.commit();
                System.out.println("✅ Import successful");

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
private static int getLocalUnitId(Connection conn) throws Exception {
    String sql = "SELECT value FROM AppConfig WHERE key='UNIT_ID'";
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            return Integer.parseInt(rs.getString(1));
        }
    }
    throw new RuntimeException("UNIT_ID not configured!");
}
    private static boolean isAllowed(String sql) {
        String s = sql.toUpperCase();
        return s.startsWith("INSERT") || s.startsWith("UPDATE");
    }
}