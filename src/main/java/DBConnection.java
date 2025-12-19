import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/product_management_db";
    private static final String USER = "product_manager_user";
    private static final String PASSWORD = "123456";

    public static Connection getDBConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to database", e);
        }
    }
}
