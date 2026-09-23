package stockmanagementsystem;

public class HDD extends Storage{
    public String isInternal;
    @Override
    public String getDetails() {
        return "HDD: " + name + "Capacity: " + capacity + "Speed: " + speed + "Internal: " + isInternal;
    }
    @Override
    protected Object getProductType() {
        return "HDD";
    }

}
