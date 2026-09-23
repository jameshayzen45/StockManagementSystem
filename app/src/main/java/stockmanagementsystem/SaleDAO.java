package stockmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    
    public boolean saveSale(Sale sale) {
        Connection conn = null;
        PreparedStatement saleStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert sale record
            String saleQuery = "INSERT INTO sales (sale_date, employee_username, total_amount) VALUES (?, ?, ?)";
            saleStmt = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);
            saleStmt.setTimestamp(1, new Timestamp(sale.getSaleDate().getTime()));
            saleStmt.setString(2, sale.getEmployeeUsername());
            saleStmt.setDouble(3, sale.getTotalAmount());
            saleStmt.executeUpdate();
            
            // Get generated sale ID
            generatedKeys = saleStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int saleId = generatedKeys.getInt(1);
                sale.setId(saleId);
                
                // Insert sale items
                String itemQuery = "INSERT INTO sale_items (sale_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
                itemStmt = conn.prepareStatement(itemQuery);
                
                for (SaleItem item : sale.getItems()) {
                    itemStmt.setInt(1, saleId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setString(3, item.getProductName());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.setInt(5, item.getQuantity());
                    itemStmt.addBatch();
                    
                    // Update product stock
                    updateProductStock(conn, item.getProductId(), item.getQuantity());
                }
                
                itemStmt.executeBatch();
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (itemStmt != null) itemStmt.close();
                if (saleStmt != null) saleStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
    }
    
    private void updateProductStock(Connection conn, int productId, int quantitySold) throws SQLException {
        String query = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantitySold);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
    
    public List<Sale> getSalesByEmployee(String username) {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM sales WHERE employee_username = ? ORDER BY sale_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sale.setEmployeeUsername(rs.getString("employee_username"));
                sale.totalAmount = rs.getDouble("total_amount");
                
                // Load sale items
                loadSaleItems(sale);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sales;
    }
    
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM sales ORDER BY sale_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sale.setEmployeeUsername(rs.getString("employee_username"));
                sale.totalAmount = rs.getDouble("total_amount");
                loadSaleItems(sale);
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
    
    public List<Sale> getSalesByDateRange(java.util.Date fromDate, java.util.Date toDate) {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sale.setEmployeeUsername(rs.getString("employee_username"));
                sale.totalAmount = rs.getDouble("total_amount");
                
                // Load sale items
                loadSaleItems(sale);
                
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sales;
    }
    
    private void loadSaleItems(Sale sale) {
        String query = "SELECT * FROM sale_items WHERE sale_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, sale.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setId(rs.getInt("id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                
                sale.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}