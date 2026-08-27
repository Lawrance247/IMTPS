// Sisa (comments are for me not to forget design choices)
package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import models.Task;



/**
 * In-memory storage for {@link Task}s, keyed by task ID.
 *
 * <p>This is the app's only persistence layer for the MVP — there is no database, so every {@code Task} lives only as long as the app is running.
 * {@code TaskDB} is intentionally "dumb": it does no validation of its own (that's {@code services.TaskManager}'s job) and just enforces the basic
 * invariants of a keyed store — you can't add a task whose ID already exists, and you can't remove/update one that doesn't.</p>
 *
 * <p><b>Collaborators:</b> constructed once by {@code app.App} and wrapped by the single {@code services.TaskManager} instance; never accessed
 * directly by controllers.</p>
 */
public class TaskDB 
{
    // HashMaps are quiker for lookups than lists; no need to scan the list so lookups are O(1) time
    private final Map<String, Task> tasks = new HashMap<>();


    /**
     * Stores a new task.
     *
     * @param task the task to store
     * @throws IllegalArgumentException if a task with the same ID is already stored
     */
    public void add(Task task) 
    {
        if (tasks.containsKey(task.getId())) 
        {
            throw new IllegalArgumentException("Task with id " + task.getId() + " already exists");
        }
        tasks.put(task.getId(), task);
    }



    /**
     * Removes the task with the given ID.
     *
     * @param id the ID of the task to remove
     * @throws NoSuchElementException if no task with that ID exists
     */
    public void remove(String id) 
    {
        // check before removing so that no silent nulls are thrown
        if (!tasks.containsKey(id)) 
        {
            throw new NoSuchElementException("No task with id " + id);
        }
        tasks.remove(id);
    }


    
    /**
     * Overwrites the stored task that shares this task's ID with the given,
     * presumably-modified, task object.
     *
     * @param task the (already-modified) task to save
     * @throws NoSuchElementException if no task with a matching ID is currently stored
     */
    public void update(Task task) 
    {
        // check before removing so that no silent nulls are thrown (Guard clause)
        if (!tasks.containsKey(task.getId())) 
        {
            throw new NoSuchElementException("No task with id " + task.getId());
        }
        // updating entails inserting or overwritng the key value pair  (Guard clause)
        tasks.put(task.getId(), task);
    }


    /**
     * @param id the task ID to look up
     * @return the matching task, or {@code null} if no task with that ID exists
     */
    public Task findById(String id) 
    {
        // returns null if no such task exists (UI implementantion of this should handle that exception)
        return tasks.get(id);
    }


    
    
    /**
     * @return a new, independent list containing every stored task. Mutating
     *         the returned list does not affect this {@code TaskDB} — callers
     *         must go through {@link #add}/{@link #remove}/{@link #update} to
     *         change what's stored.
     */
    public List<Task> findAll() 
    {
        /** returns an ArayList, this is so that the person calling findAll gets a coppy and any changes dont affect TaskDB directly; removals handled by task manager */
        return new ArrayList<>(tasks.values());
    }
}