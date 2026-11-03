package taskmanagement;

public class UserSettings {
    private final String userId;
    private String theme;
    private String defaultStrategy;
    private int defaultFocusMinutes;
    private boolean dailyReminder;

    public UserSettings(String userId, String theme, String defaultStrategy,
                        int defaultFocusMinutes, boolean dailyReminder) {
        this.userId = userId;
        this.theme = theme;
        this.defaultStrategy = defaultStrategy;
        this.defaultFocusMinutes = defaultFocusMinutes;
        this.dailyReminder = dailyReminder;
    }

    public String getUserId() { return userId; }
    public String getTheme() { return theme; }
    public String getDefaultStrategy() { return defaultStrategy; }
    public int getDefaultFocusMinutes() { return defaultFocusMinutes; }
    public boolean isDailyReminder() { return dailyReminder; }
}
