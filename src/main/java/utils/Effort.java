package utils;

public enum Effort 
{
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int weight;

    Effort(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
    
}

