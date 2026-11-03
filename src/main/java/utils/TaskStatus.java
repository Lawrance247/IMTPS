package utils;

/**
 * A task's lifecycle state. Set to {@code TO_DO} by default when a {@code Task} is created (see {@code Task}'s constructor); {@code DONE}
 * is checked by {@code Task#isOverdue()} to avoid flagging completed tasks as overdue. {@code IN_PROGRESS} is defined but not currently set or read
 * anywhere else in the app.
 */
public enum TaskStatus 
{
    DONE,
    IN_PROGRESS,
    TO_DO,   
}

