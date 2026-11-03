package utils;


/**
 * A three-level weighting (LOW/MEDIUM/HIGH) used independently for both a task's importance and its effort, each carrying a numeric
 * {@link #getWeight()} (1/2/3) that the scheduling strategies use directly in their scoring maths.
 */
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
    /** @return this weight's numeric value (1 for LOW, 2 for MEDIUM, 3 for HIGH). */
    public int getWeight()
    {
        return weight;
    }
}
