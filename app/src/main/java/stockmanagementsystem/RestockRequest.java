package stockmanagementsystem;

import java.time.LocalDateTime;

public class RestockRequest {
    private int id;
    private int productId;
    private String productName;
    private int requestedQuantity;
    private String employeeUsername;
    private LocalDateTime requestDate;
    private String status;

    // Constructor for creating a new request
    public RestockRequest(int productId, String productName, int requestedQuantity, String employeeUsername) {
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.employeeUsername = employeeUsername;
        this.requestDate = LocalDateTime.now();
        this.status = "Pending";
    }

    // Constructor for loading from database
    public RestockRequest(int id, int productId, String productName, int requestedQuantity, 
                         String employeeUsername, LocalDateTime requestDate, String status) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.employeeUsername = employeeUsername;
        this.requestDate = requestDate;
        this.status = status;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return productName + " - Qty: " + requestedQuantity + " - Status: " + status;
    }
}