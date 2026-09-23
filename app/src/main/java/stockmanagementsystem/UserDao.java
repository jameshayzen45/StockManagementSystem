package stockmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

public class UserDao {
    
    public User findByUsername(String username) {
        String query = "Select * from users where username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()){
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(rs.getString("user_type"));
                    return user;
                }
             }catch (SQLException e){
                e.printStackTrace();
             }

    // Temporary test accounts 
    if (username.equals("james")) {
        return new User(1, "james", "admin123", "admin@example.com", "Administrator");
    } else if (username.equals("employee")) {
        return new User(2, "employee", "emp123", "employee@example.com", "Employee");
    }
    return null;
}

    public boolean verifyPassword(User user, String password){
        return user.getPassword().equals(password);
    }
    
    public String generateResetToken(int userId) {
        String token = UUID.randomUUID().toString();
        String query = "UPDATE users SET reset_token = ?, token_expiry = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setString(1, token);
                stmt.setInt(2, userId);
                stmt.executeUpdate();

                return token;
             } catch (SQLException e){
                e.printStackTrace();
             }
        return null;

    }

    public void saveResetToken(int userId, String resetToken) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET reset_token = ?, token_expiry = ? WHERE id = ?")) {
            
            // Set token expiry to 24 hours 
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + 86400000);
            
            stmt.setString(1, resetToken);
            stmt.setTimestamp(2, expiry);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        System.out.println("Reset token for user " + userId + ": " + resetToken);
    }

    public boolean verifyResetToken(String token) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE reset_token = ? AND token_expiry > ?")) {
            
            stmt.setString(1, token);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First verify the token is valid
            if (!verifyResetToken(token)) {
                return false;
            }
            
            // Update the password and clear the token
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET password = ?, reset_token = NULL, token_expiry = NULL WHERE reset_token = ?");
            stmt.setString(1, newPassword); //  this would be hashed
            stmt.setString(2, token);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password, email, user_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUserType());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
