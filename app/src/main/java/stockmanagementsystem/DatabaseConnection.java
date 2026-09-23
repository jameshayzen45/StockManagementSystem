package stockmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
    private static final
    String URL = "jdbc:mysql://localhost:3306/stock_management_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "james123";


    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            String query = "select id " +
            "from products;";
            Connection connection = null;
            Statement stmt = null;
            // Removed unused ResultSet rs = null;

            try{
                // step 2: create connection
                System.out.println("Connecting to the MySQL database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("MySQL database connected!");
                // step 3: create statement
                stmt = connection.createStatement();
                // step 4: execute queries
                ResultSet resultSet = stmt.executeQuery(query);
                // print results
                while (resultSet.next())
                {
                System.out.print(resultSet.getString(1));
                System.out.print(" ");
                System.out.println(resultSet.getString(2));
                }
                stmt.close();
                }
                catch (SQLException sqlE)
                {
                System.out.println(sqlE.toString());
                }
                finally
                {
                // step 5: close connection
                System.out.println("Closing connection...");
                if(connection != null)
                {
                try
                {
                connection.close();
                }
                catch (SQLException ignore)
                {}
                connection= null;
                }
                }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
