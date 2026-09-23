package stockmanagementsystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {
    private static final
    String URL = "jdbc:mysql://127.0.0.1:3306/stock_management_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "james123";


    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    @Test
    public void testDatabaseConnection() {
        try {
            Connection connection = getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(1), "Connection should be valid");
            connection.close();
        } catch (SQLException e) {
            fail("Failed to connect to the database: " + e.getMessage());
        }
    }
}




