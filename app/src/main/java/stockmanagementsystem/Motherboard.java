package stockmanagementsystem;


public class Motherboard extends Product {
    String socketType;
    
    public String getSocketType() {
        return socketType;
    }
    
    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }
    
    @Override
    public String getDetails() {
        return "Motherboard: " + name + ", Socket: " + socketType;
    }

    @Override
    protected Object getProductType() {
        return "Motherboard";
    }
}