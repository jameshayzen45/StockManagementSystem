package stockmanagementsystem;

public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    
    public SaleItem() {
    }
    
    public SaleItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getSellingPrice();
        this.quantity = quantity;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSaleId() {
        return saleId;
    }
    
    public void setSaleId(int saleId) {
        this.saleId = saleId;
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
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }

    public Product getProduct() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProduct'");
    }
}