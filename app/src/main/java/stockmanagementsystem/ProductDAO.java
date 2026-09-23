package stockmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductDAO {

    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (name, brand, cost_price, selling_price, stock_quantity, product_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, product.getName());
                stmt.setString(2, product.getBrand());
                stmt.setDouble(3, product.getCostPrice());
                stmt.setDouble(4, product.getSellingPrice());
                stmt.setInt(5, product.getStockQuantity());
                stmt.setString(6, product.getClass().getSimpleName());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;

             }catch (SQLException e){
                e.printStackTrace();
                return false;
             }
    }

    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

                while (rs.next()){
                    String productType = rs.getString("product_type");
                    Product product = createProductByType(productType);
                    
                    if (product != null) {
                        product.setId(rs.getInt("id"));
                        product.setName(rs.getString("name"));
                        product.setBrand(rs.getString("brand"));
                        
                        product.setCostPrice(rs.getDouble("cost_price"));
                        product.setSellingPrice(rs.getDouble("selling_price"));
                        product.setStockQuantity(rs.getInt("stock_quantity"));

                        products.add(product);
                    }
                }
             } catch (SQLException e) {
                e.printStackTrace();
             }
        return products;
    }
    
    
    private Product createProductByType(String type){
        switch (type) {
            case "CPU":
                return new CPU();
            case "GPU":
                return new GPU();
            case "RAM":
                return new RAM();
           
            case "SSD":
                return new SSD();
            case "HDD":
                return new HDD();
            
            case "Motherboard":
                
                return createMotherboard();
            case "PSU":
                
                return createPSU();
            default:
                return null;
        }
    }
    
   
    private Product createMotherboard() {
    
        return new Product() {
            private String socketType;
            
            @Override
            public String getDetails() {
                return "Motherboard: " + name + ", Socket: " + socketType;
            }

            @Override
            protected Object getProductType() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getProductType'");
            }
        };
    }
    
    private Product createPSU() {
        // Create a simple implementation of PSU
        return new Product() {
            private int wattage;
            
            @Override
            public String getDetails() {
                return "PSU: " + name + ", Wattage: " + wattage + "W";
            }

            @Override
            protected Object getProductType() {
                return "PSU";
            }
        };
    }
    
   

    
    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String productType = rs.getString("product_type");
                Product product = createProductByType(productType);
                
                if (product != null) {
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setBrand(rs.getString("brand"));
                    product.setCostPrice(rs.getDouble("cost_price"));
                    product.setSellingPrice(rs.getDouble("selling_price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET name = ?, brand = ?, cost_price = ?, " +
                      "selling_price = ?, stock_quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getBrand());
            stmt.setDouble(3, product.getCostPrice());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setInt(6, product.id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE name LIKE ? OR brand LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String productType = rs.getString("product_type");
                Product product = createProductByType(productType);
                
                if (product != null) {
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setBrand(rs.getString("brand"));
                    product.setCostPrice(rs.getDouble("cost_price"));
                    product.setSellingPrice(rs.getDouble("selling_price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
}
