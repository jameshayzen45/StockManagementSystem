package stockmanagementsystem;

public class RAM extends Product {
    int capacity;
    private String speed;

    @Override
    public String getDetails() {
        return "RAM: " + name + "Capacity: " + capacity + "GB, Speed:" + speed + "Mhz";
    }

    @Override
    protected Object getProductType() {
        return "RAM";   
    }
}
