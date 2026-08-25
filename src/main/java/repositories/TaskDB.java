// Sisa (comments are for me not to forget design choices)
package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import models.Task;

public class TaskDB 
{
    // HashMaps are quiker for lookups than lists; no need to scan the list so lookups are O(1) time
    private final Map<String, Task> tasks = new HashMap<>();

    public void add(Task task) 
    {
        if (tasks.containsKey(task.getId())) 
        {
            throw new IllegalArgumentException("Task with id " + task.getId() + " already exists");
        }
        tasks.put(task.getId(), task);
    }

    // check before removing so that no silent nulls are thrown
    public void remove(String id) 
    {
        if (!tasks.containsKey(id)) 
        {
            throw new NoSuchElementException("No task with id " + id);
        }
        tasks.remove(id);
    }

    // check before removing so that no silent nulls are thrown (Guard clause)
    public void update(Task task) 
    {
        if (!tasks.containsKey(task.getId())) 
        {
            throw new NoSuchElementException("No task with id " + task.getId());
        }
        // updating entails inserting or overwritng the key value pair  (Guard clause)
        tasks.put(task.getId(), task);
    }

    // returns null if no such task exists (UI implementantion of this should handle that exception)
    public Task findById(String id) 
    {
        return tasks.get(id);
    }

    // returns an ArayList, this is so that the person calling findAll gets a coppy and any changes dont affect TaskDB directly; removals handled by task manager
    public List<Task> findAll() 
    {
        return new ArrayList<>(tasks.values());
    }
}