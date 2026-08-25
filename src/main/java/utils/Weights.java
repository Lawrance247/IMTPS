package utils;

public enum Weights 
{
    // constants
    LOW(1),
    MEDIUM(2),
    HIGH(3);
    
    // fields
    private final int weight;

    // constructo
    Weights(int weight)
    {
        this.weight =weight;
    }
     
    // getter
    public int getWeight()
    {
        return weight;
    }
}
