package utils;

public enum Priority 
{
    // constants
    LOW(1),
    MEDIUM(2),
    HIGH(3);
    
    // fields
    private final int weight;

    // constructo
    Priority(int weight)
    {
        this.weight =weight;
    }
     
    // getter
    public int getWeight()
    {
        return weight;
    }
}
