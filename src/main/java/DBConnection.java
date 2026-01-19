import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url = System.getenv("JDBC_URL");
            String user = System.getenv("USERNAME");
            String pwd = System.getenv("PASSWORD");

            Connection connection = DriverManager.getConnection(url, user, pwd);

            // 🔥 FORCER L’ENCODAGE CÔTÉ CLIENT JDBC
            try (Statement st = connection.createStatement()) {
                st.execute("SET client_encoding = 'UTF8'");
            }


            return connection;

        } catch (Exception e) {
            throw new RuntimeException("Connexion échouée", e);
        }
    }
}
