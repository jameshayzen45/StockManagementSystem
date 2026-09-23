package stockmanagementsystem;
public class PSU extends Product {
    int wattage;
    
    public int getWattage() {
        return wattage;
    }
    
    public void setWattage(int wattage) {
        this.wattage = wattage;
    }
    
    @Override
    public String getDetails() {
        return "PSU: " + name + ", Wattage: " + wattage + "W";
    }

    @Override
    protected Object getProductType() {
        return "PSU";
    }
}