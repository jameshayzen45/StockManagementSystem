package stockmanagementsystem;
public class CPU extends Product {
    String socketType;
    int coreCount;
    int clockSpeed;
    
    @Override
    public String getDetails() {
        return "CPU: " + name + "Socket: " + socketType + ", Cores:" + coreCount + "Core Speed: " + clockSpeed + "Ghz";
    }

    @Override
    protected Object getProductType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductType'");
    }
}

