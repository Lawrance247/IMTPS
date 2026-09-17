import {
  CalendarDays,
  ChevronDown,
  CirclePlus,
  Clock3,
  Trash2,
  Search,
  SlidersHorizontal,
} from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import AddTaskModal from "../components/tasks/AddTaskModal";
import "./TasksPage.css";

type Category = "Academic" | "Family" | "Health" | "Leisure";
type Status = "To Do" | "In Progress" | "Completed";
type Weight = "High" | "Medium" | "Low";

type Task = {
  id: string;
  title: string;
  description: string;
  category: Category;
  deadline: string;
  time: string;
  duration: number;
  importance: Weight;
  effort: Weight;
  status: Status;
};

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

const API_URL = "http://localhost:8080/api/tasks";

function titleCase(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

function fromApiTask(task: ApiTask): Task {
  // The database contains some older test dates, so support both formats.
  const normalisedDeadline = task.deadline.replace(/\//g, "-").replace(" ", "T");
  const [date = task.deadline, time = ""] = normalisedDeadline.split("T");

  return {
    id: task.id,
    title: task.title,
    description: task.description ?? "",
    category: titleCase(task.category) as Category,
    deadline: date,
    time: time.slice(0, 5),
    duration: task.durationMinutes,
    importance: titleCase(task.importance) as Weight,
    effort: titleCase(task.effort) as Weight,
    status:
      task.status === "DONE"
        ? "Completed"
        : task.status === "IN_PROGRESS"
          ? "In Progress"
          : "To Do",
  };
}

const categories: Array<"All" | Category> = [
  "All",
  "Academic",
  "Family",
  "Health",
  "Leisure",
];

export default function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<"All" | Category>("All");
  const [status, setStatus] = useState<"All" | Status>("All");
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadTasks();
  }, []);

  async function loadTasks() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(API_URL);
      if (!response.ok) {
        throw new Error("Could not load tasks");
      }

      const data: ApiTask[] = await response.json();
      setTasks(data.map(fromApiTask));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not load tasks");
    } finally {
      setLoading(false);
    }
  }

  const filteredTasks = useMemo(() => {
    return tasks.filter((task) => {
      const matchesSearch =
        task.title.toLowerCase().includes(query.toLowerCase()) ||
        task.description.toLowerCase().includes(query.toLowerCase());
      const matchesCategory = category === "All" || task.category === category;
      const matchesStatus = status === "All" || task.status === status;
      return matchesSearch && matchesCategory && matchesStatus;
    });
  }, [tasks, query, category, status]);

  async function toggleComplete(task: Task) {
    const newStatus: Status =
      task.status === "Completed" ? "To Do" : "Completed";

    try {
      setError("");

      const response = await fetch(`${API_URL}/${task.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          title: task.title,
          description: task.description,
          category: task.category.toUpperCase(),
          deadline: `${task.deadline}T${task.time || "00:00"}:00`,
          durationMinutes: task.duration,
          importance: task.importance.toUpperCase(),
          effort: task.effort.toUpperCase(),
          status: newStatus === "Completed" ? "DONE" : "TO_DO",
        }),
      });

      if (!response.ok) {
        throw new Error("Could not update task");
      }

      const updated: ApiTask = await response.json();
      setTasks((current) =>
        current.map((item) =>
          item.id === task.id ? fromApiTask(updated) : item,
        ),
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not update task");
    }
  }


  async function deleteTask(task: Task) {
    try {
      setError("");
      const response = await fetch(`${API_URL}/${task.id}`, { method: "DELETE" });
      if (!response.ok) throw new Error("Could not delete task");
      setTasks((current) => current.filter((item) => item.id !== task.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete task");
    }
  }

  return (
    <div className="tasks-page">
      <header className="tasks-header">
        <div>
          <span className="tasks-eyebrow">Your task space</span>
          <h1>Tasks</h1>
          <p>Create, organise and keep track of what needs your attention.</p>
        </div>
        <button className="tasks-add-button" onClick={() => setShowForm(true)}>
          <CirclePlus size={18} />
          Add Task
        </button>
      </header>

      <section className="tasks-toolbar">
        <label className="tasks-search">
          <Search size={18} />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search tasks..."
          />
        </label>

        <label className="tasks-select">
          <SlidersHorizontal size={16} />
          <select
            value={status}
            onChange={(event) =>
              setStatus(event.target.value as "All" | Status)
            }
          >
            <option value="All">All statuses</option>
            <option value="To Do">To Do</option>
            <option value="In Progress">In Progress</option>
            <option value="Completed">Completed</option>
          </select>
          <ChevronDown size={15} />
        </label>
      </section>

      <div className="category-tabs">
        {categories.map((item) => (
          <button
            key={item}
            className={`category-tab ${category === item ? "active" : ""} ${
              item !== "All" ? item.toLowerCase() : ""
            }`}
            onClick={() => setCategory(item)}
          >
            {item}
          </button>
        ))}
      </div>

      {error && <div className="tasks-empty"><p>{error}</p></div>}
      {loading && <div className="tasks-empty"><p>Loading tasks...</p></div>}

      <section className="task-list">
        <div className="task-list-heading">
          <span>{filteredTasks.length} tasks</span>
          <span>Sorted by deadline</span>
        </div>

        {filteredTasks.map((task) => (
          <article
            className={`task-row ${
              task.status === "Completed" ? "completed" : ""
            }`}
            key={task.id}
          >
            <button
              className="task-check"
              onClick={() => toggleComplete(task)}
              aria-label={`Mark ${task.title} as completed`}
            >
              {task.status === "Completed" && "✓"}
            </button>

            <div className="task-main">
              <div className="task-title-line">
                <h3>{task.title}</h3>
                <span className={`task-category ${task.category.toLowerCase()}`}>
                  {task.category}
                </span>
              </div>
              <p>{task.description}</p>
            </div>

            <div className="task-date">
              <CalendarDays size={15} />
              <div>
                <strong>{task.deadline}</strong>
                <span>{task.time}</span>
              </div>
            </div>

            <div className="task-duration">
              <Clock3 size={15} />
              <span>{task.duration} min</span>
            </div>

            <span className={`importance ${task.importance.toLowerCase()}`}>
              {task.importance}
            </span>

            <span
              className={`task-status ${task.status
                .toLowerCase()
                .replace(" ", "-")}`}
            >
              {task.status}
            </span>

            <button
              className="task-menu task-delete-button"
              aria-label={`Delete ${task.title}`}
              title="Delete task"
              onClick={() => deleteTask(task)}
            >
              <Trash2 size={18} />
            </button>
          </article>
        ))}

        {filteredTasks.length === 0 && (
          <div className="tasks-empty">
            <Search size={28} />
            <h3>No tasks found</h3>
            <p>Try another search or filter.</p>
          </div>
        )}
      </section>

      {showForm && (
        <AddTaskModal
          onClose={() => setShowForm(false)}
          onTaskAdded={(created) =>
            setTasks((current) => [...current, fromApiTask(created)])
          }
        />
      )}
    </div>
  );
}
