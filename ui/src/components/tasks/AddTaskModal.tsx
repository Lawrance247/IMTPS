import { FormEvent, useState } from "react";
import "../../pages/TasksPage.css";

type ApiTask = {
  id: string;
  title: string;
  description: string;
  category: string;
  deadline: string;
  durationMinutes: number;
  importance: string;
  effort: string;
  status: string;
};

type AddTaskModalProps = {
  onClose: () => void;
  onTaskAdded?: (task: ApiTask) => void;
};

const API_URL = "http://localhost:8080/api/tasks";

export default function AddTaskModal({ onClose, onTaskAdded }: AddTaskModalProps) {
  const [error, setError] = useState("");

  async function addTask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const deadline = String(form.get("deadline"));
    const time = String(form.get("time"));

    try {
      setError("");

      const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          title: String(form.get("title")),
          description: String(form.get("description") ?? ""),
          category: String(form.get("category")).toUpperCase(),
          deadline: `${deadline}T${time}:00`,
          durationMinutes: Number(form.get("duration")),
          importance: String(form.get("importance")).toUpperCase(),
          effort: String(form.get("effort")).toUpperCase(),
        }),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => null);
        throw new Error(body?.error ?? "Could not create task");
      }

      const created: ApiTask = await response.json();
      onTaskAdded?.(created);
      formElement.reset();
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not create task");
    }
  }

  return (
    <div className="task-modal-backdrop" onMouseDown={onClose}>
      <div className="task-modal" onMouseDown={(event) => event.stopPropagation()}>
        <div className="task-modal-heading">
          <div>
            <span>New task</span>
            <h2>Add something to your day</h2>
          </div>
          <button onClick={onClose}>×</button>
        </div>

        {error && <div className="tasks-empty"><p>{error}</p></div>}

        <form className="task-form" onSubmit={addTask}>
          <label>
            <span>Task name</span>
            <input name="title" placeholder="e.g. Study for INF3014F" autoFocus required />
          </label>

          <label>
            <span>Description <small>optional</small></span>
            <textarea name="description" placeholder="Add a few details..." rows={3} />
          </label>

          <div className="task-form-row">
            <label>
              <span>Category</span>
              <select name="category" defaultValue="Academic">
                <option>Academic</option>
                <option>Family</option>
                <option>Health</option>
                <option>Leisure</option>
              </select>
            </label>
            <label>
              <span>Deadline</span>
              <input name="deadline" type="date" required />
            </label>
          </div>

          <div className="task-form-row">
            <label>
              <span>Time</span>
              <input name="time" type="time" required />
            </label>
            <label>
              <span>Duration</span>
              <select name="duration" defaultValue="60">
                <option value="15">15 minutes</option>
                <option value="30">30 minutes</option>
                <option value="45">45 minutes</option>
                <option value="60">1 hour</option>
                <option value="90">1 hour 30 min</option>
                <option value="120">2 hours</option>
              </select>
            </label>
          </div>

          <div className="task-form-row">
            <label>
              <span>Importance</span>
              <select name="importance" defaultValue="Medium">
                <option>Low</option>
                <option>Medium</option>
                <option>High</option>
              </select>
            </label>
            <label>
              <span>Effort</span>
              <select name="effort" defaultValue="Medium">
                <option>Low</option>
                <option>Medium</option>
                <option>High</option>
              </select>
            </label>
          </div>

          <div className="task-form-actions">
            <button type="button" onClick={onClose}>Cancel</button>
            <button type="submit">Add Task</button>
          </div>
        </form>
      </div>
    </div>
  );
}
