package stockmanagementsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestockRequestDAO {
    
    // Add a new restock request to the database
    public boolean addRequest(RestockRequest request) {
        String sql = "INSERT INTO restock_requests (product_id, product_name, requested_quantity, employee_username) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, request.getProductId());
            pstmt.setString(2, request.getProductName());
            pstmt.setInt(3, request.getRequestedQuantity());
            pstmt.setString(4, request.getEmployeeUsername());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error adding restock request: " + e.getMessage());
            return false;
        }
    }
    
    // Get all restock requests
    public List<RestockRequest> getAllRequests() {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM restock_requests ORDER BY request_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RestockRequest request = new RestockRequest(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("requested_quantity"),
                    rs.getString("employee_username"),
                    rs.getTimestamp("request_date").toLocalDateTime(),
                    rs.getString("status")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving restock requests: " + e.getMessage());
        }
        
        return requests;
    }
    
    // Get pending restock requests
    public List<RestockRequest> getPendingRequests() {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM restock_requests WHERE status = 'Pending' ORDER BY request_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RestockRequest request = new RestockRequest(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("requested_quantity"),
                    rs.getString("employee_username"),
                    rs.getTimestamp("request_date").toLocalDateTime(),
                    rs.getString("status")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving pending restock requests: " + e.getMessage());
        }
        
        return requests;
    }
    
    // Update request status
    public boolean updateRequestStatus(int requestId, String newStatus) {
        String sql = "UPDATE restock_requests SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating restock request status: " + e.getMessage());
            return false;
        }
    }
    
    // Get restock requests by employee
    public List<RestockRequest> getRequestsByEmployee(String username) {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM restock_requests WHERE employee_username = ? ORDER BY request_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RestockRequest request = new RestockRequest(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("requested_quantity"),
                        rs.getString("employee_username"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                    );
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee restock requests: " + e.getMessage());
        }
        
        return requests;
    }
}