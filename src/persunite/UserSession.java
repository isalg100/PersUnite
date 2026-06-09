package persunite;

public class UserSession {

    // =========================================
    // USER
    // =========================================

    public static int userId;

    public static String username;

    public static String role;

    public static String fullName;

    public static String matrim;

    // =========================================
    // AGENT
    // =========================================

    public static String agentNom;

    public static String agentPrenom;

    public static int affecx;

    // =========================================
    // UNITE
    // =========================================

    public static int uniteId;

    public static String uniteName;

    // =========================================
    // RESET SESSION
    // =========================================

    public static void clear() {

        userId = 0;

        username = null;

        role = null;

        fullName = null;

        matrim = null;

        agentNom = null;

        agentPrenom = null;

        affecx = 0;

        uniteId = 0;

        uniteName = null;
    }

    // =========================================
    // IS ADMIN
    // =========================================

    public static boolean isAdmin() {

        return "SUPER_ADMIN".equals(role);
    }
}