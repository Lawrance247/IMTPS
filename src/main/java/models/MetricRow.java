package models;


/**
 * A single display-ready row for the metrics table on the comparison view.
 *
 * <p>Unlike {@link ScheduleMetrics} (which stores raw numeric metric values for one schedule), {@code MetricRow} is a pure presentation/view-model
 * object: one row = one metric, already formatted as strings, with both strategies' values side by side plus a human-readable name, definition,
 * and "which direction is better" label. It exists purely so {@code controllers.ComparisonController} has something simple to bind
 * directly to the {@code TableView<MetricRow>} columns.</p>
 *
 * <p><b>Collaborators:</b> built row-by-row inside {@code controllers.ComparisonController#initialize()}, using values pulled
 * from a pair of {@link ScheduleMetrics} instances (via {@link ComparisonResult}); consumed only by the metrics {@code TableView}
 * in the comparison view.</p>
 */
public class MetricRow {

    private final String metricName;
    private final String definition;
    private final String direction;
    private final String valueA;
    private final String valueB;


    /**
     * @param metricName the metric's display name, e.g. "Deadline Clashes"
     * @param definition a plain-language explanation of what the metric measures
     * @param direction  which direction is better for this metric, e.g. "Lower" or "Higher"
     * @param valueA     this metric's formatted value for Strategy A's schedule
     * @param valueB     this metric's formatted value for Strategy B's schedule
     */
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
