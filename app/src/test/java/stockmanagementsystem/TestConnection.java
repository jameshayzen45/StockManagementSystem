package stockmanagementsystem;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Attempting to connect to database...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Connection successful!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
