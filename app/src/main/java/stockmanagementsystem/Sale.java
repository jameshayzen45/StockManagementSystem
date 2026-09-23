package stockmanagementsystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sale {
    private int id;
    private Date saleDate;
    private String employeeUsername;
    private List<SaleItem> items;
    double totalAmount;
    
    public Sale() {
        this.saleDate = new Date();
        this.items = new ArrayList<SaleItem>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getSaleDate() {
        return saleDate;
    }
    
    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
    
    public String getEmployeeUsername() {
        return employeeUsername;
    }
    
    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }
    
    public List<SaleItem> getItems() {
        return items;
    }
    
    public void addItem(SaleItem item) {
        this.items.add(item);
        calculateTotal();
    }
    
    public void removeItem(SaleItem item) {
        this.items.remove(item);
        calculateTotal();
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    private void calculateTotal() {
        this.totalAmount = 0;
        for (SaleItem item : items) {
            this.totalAmount += item.getSubtotal();
        }
    }
}