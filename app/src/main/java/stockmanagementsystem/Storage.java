package stockmanagementsystem;

public abstract class Storage extends Product {
    protected String capacity;
    protected String speed;
    protected boolean isInternal;

    public abstract String getDetails();
    

}
