package stockmanagementsystem;

public abstract class Product {
protected int id;
protected String name;
protected String brand;
protected double costPrice; 
protected double sellingPrice;
protected int stockQuantity; 
protected String imagePath;

public abstract String getDetails();


public void setId(int id) {
    this.id = id;
}

public void setName(String name) {
    this.name = name;
}

public void setBrand(String brand) {
    this.brand = brand;
}

public void setCostPrice(double costPrice) {
    this.costPrice = costPrice; 
}

public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
}

public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
}

public String getName() {
    return name;
}

public String getBrand() {
    return brand;
}

public double getCostPrice() {
    return costPrice;
}

public double getSellingPrice() {
    return sellingPrice;
}

public int getStockQuantity() {
    return stockQuantity;
}

public int getId() {
    return id;
}


protected abstract Object getProductType();
}