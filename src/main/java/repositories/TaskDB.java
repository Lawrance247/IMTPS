// Sisa (comments are for me not to forget design choices)
package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import models.Task;

public class TaskDB {

    // HashMaps are quicker for lookups than lists; no need to scan the list
    // so lookups are O(1) time.
    private final Map<String, Task> tasks = new HashMap<>();

    public void add(Task task) {

        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException(
                "Task with id " + task.getId() + " already exists"
            );
        }

        tasks.put(task.getId(), task);
    }

    // Check before removing so that no silent nulls are thrown.
    public void remove(String id) {

        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException(
                "No task with id " + id
            );
        }

        tasks.remove(id);
    }

    // Check before updating so that an invalid task cannot be silently added.
    // This is a guard clause.
    public void update(Task task) {

        if (!tasks.containsKey(task.getId())) {
            throw new NoSuchElementException(
                "No task with id " + task.getId()
            );
        }

        // Updating entails inserting or overwriting the key-value pair.
        tasks.put(task.getId(), task);
    }

    // Returns null if no such task exists.
    // UI implementation should handle this appropriately.
    public Task findById(String id) {
        return tasks.get(id);
    }

    // Returns an ArrayList so that the person calling findAll()
    // gets a copy and changes do not affect TaskDB directly.
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
}