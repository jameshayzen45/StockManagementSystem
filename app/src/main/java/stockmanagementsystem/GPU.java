package stockmanagementsystem;

public class GPU extends Product {
    private String memoryType;
    int memorySize;
    private String coreClock;
    @Override
    public String getDetails() {
        return "GPU: " + name + "Memory Type: " + memoryType + ", Memory Size: " + memorySize + "GB, Core Clock: " + coreClock + "Mhz";
    }
    @Override
    protected Object getProductType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductType'");
    }


}
