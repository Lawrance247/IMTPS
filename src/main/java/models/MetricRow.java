package models;

public class MetricRow {

    private final String metricName;
    private final String definition;
    private final String direction;
    private final String valueA;
    private final String valueB;

    public MetricRow(String metricName, String definition, String direction, String valueA, String valueB) {
        this.metricName = metricName;
        this.definition = definition;
        this.direction = direction;
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getDefinition() {
        return definition;
    }

    public String getDirection() {
        return direction;
    }

    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }
}
