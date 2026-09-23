package stockmanagementsystem;

public class SSD extends Storage{
    String ConnectionType;
    
    @Override
    public String getDetails() {
        return "SSD: " + name + "Connection Type: " + ConnectionType;
    }

    @Override
    protected Object getProductType() {
        return "SSD";
    }
}
