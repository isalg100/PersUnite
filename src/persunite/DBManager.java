package persunite;

import java.awt.BorderLayout;
import java.io.*;
import java.sql.*;
import javax.swing.*;

public class DBManager {

    private static final String APP_DIR = System.getProperty("user.dir");
    private static final String DB_PATH = APP_DIR + File.separator + "database.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    private static boolean dbInitialized = false;

    public static void initDatabase(JFrame parent) {

        if (dbInitialized) return;

        System.out.println("====================================");
        System.out.println("🚀 INIT DATABASE");
        System.out.println("APP DIR = " + APP_DIR);
        System.out.println("DB PATH = " + DB_PATH);
        System.out.println("====================================");

        ensureDatabase(parent);

        dbInitialized = true;
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }

    private static void ensureDatabase(JFrame parent) {

        JDialog dialog = new JDialog(parent, "Database Initialization", true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Initializing database...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        dialog.add(label, BorderLayout.NORTH);
        dialog.add(progressBar, BorderLayout.CENTER);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {

                publish(10);

                // 🔹 إنشاء المجلد
                File dir = new File(APP_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                    System.out.println("📁 Directory created");
                }

                publish(30);

                // 🔹 إنشاء/فتح القاعدة
                File dbFile = new File(DB_PATH);
                boolean dbExistsBefore = dbFile.exists();

                try (Connection conn = DriverManager.getConnection(DB_URL)) {

                    System.out.println("📦 DB exists before: " + dbExistsBefore);
                    System.out.println("📦 DB absolute path: " + dbFile.getAbsolutePath());

                    publish(50);

                    // 🔥 تنفيذ schema دائما (مرحلة التطوير)
                    runSchema(conn);

                    publish(80);

                    // 🔍 تحقق من الجداول
                    printTables(conn);

                }

                publish(100);
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int value = chunks.get(chunks.size() - 1);
                progressBar.setValue(value);

                if (value < 50) {
                    label.setText("Preparing environment...");
                } else if (value < 80) {
                    label.setText("Creating tables...");
                } else {
                    label.setText("Finalizing...");
                }
            }

            @Override
            protected void done() {
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                        "Database ready ✅",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }

    // =========================================
    // 🔥 RUN SCHEMA (WITH DEBUG)
    // =========================================
    private static void runSchema(Connection conn) throws Exception {

        System.out.println("⚙️ RUNNING SCHEMA...");

        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("schema.sql");
        System.out.println(
    Thread.currentThread()
        .getContextClassLoader()
        .getResource("schema.sql")
);

        System.out.println("📄 SCHEMA STREAM = " + is);

        if (is == null) {
            throw new RuntimeException("❌ schema.sql NOT FOUND in resources!");
        }

        String sql = new String(is.readAllBytes());

        System.out.println("📏 SCHEMA SIZE = " + sql.length());

        String[] queries = sql.split(";");

        try (Statement stmt = conn.createStatement()) {

            for (String q : queries) {
                q = q.trim();

                if (!q.isEmpty()) {
                    System.out.println("▶ EXEC: " + q.substring(0, Math.min(80, q.length())));
                    stmt.execute(q);
                }
            }
        }

        System.out.println("✅ SCHEMA EXECUTED");
    }

    // =========================================
    // 🔍 PRINT TABLES
    // =========================================
    private static void printTables(Connection conn) throws Exception {

        System.out.println("📋 TABLES IN DATABASE:");

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "%", null);

        boolean hasTables = false;

        while (rs.next()) {
            hasTables = true;
            System.out.println("   👉 " + rs.getString("TABLE_NAME"));
        }

        if (!hasTables) {
            System.out.println("❌ NO TABLES FOUND !");
        } else {
            System.out.println("✅ TABLES OK");
        }
    }
}