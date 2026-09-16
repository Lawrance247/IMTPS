import {
  CalendarDays,
  ChevronDown,
  CirclePlus,
  Clock3,
  MoreHorizontal,
  Search,
  SlidersHorizontal,
} from "lucide-react";
import { useMemo, useState } from "react";
import "./TasksPage.css";

type Category = "Academic" | "Family" | "Health" | "Leisure";
type Status = "To Do" | "In Progress" | "Completed";

type Task = {
  id: number;
  title: string;
  description: string;
  category: Category;
  deadline: string;
  time: string;
  duration: number;
  importance: "High" | "Medium" | "Low";
  effort: "High" | "Medium" | "Low";
  status: Status;
};

const initialTasks: Task[] = [
  {
    id: 1,
    title: "Study CSC3003S",
    description: "Prepare for TOA practice and review lecture notes.",
    category: "Academic",
    deadline: "19 Sep",
    time: "10:00",
    duration: 90,
    importance: "High",
    effort: "High",
    status: "In Progress",
  },
  {
    id: 2,
    title: "Call my parents",
    description: "Catch up with family before the weekend.",
    category: "Family",
    deadline: "20 Sep",
    time: "18:30",
    duration: 30,
    importance: "Medium",
    effort: "Low",
    status: "To Do",
  },
  {
    id: 3,
    title: "Go to gym",
    description: "Lower-body workout and stretching.",
    category: "Health",
    deadline: "20 Sep",
    time: "16:00",
    duration: 60,
    importance: "Medium",
    effort: "Medium",
    status: "To Do",
  },
  {
    id: 4,
    title: "INF3011F project work",
    description: "Continue implementation and database integration.",
    category: "Academic",
    deadline: "21 Sep",
    time: "14:00",
    duration: 120,
    importance: "High",
    effort: "High",
    status: "To Do",
  },
  {
    id: 5,
    title: "Play video games",
    description: "Take some time to recharge after studying.",
    category: "Leisure",
    deadline: "21 Sep",
    time: "20:00",
    duration: 60,
    importance: "Low",
    effort: "Low",
    status: "To Do",
  },
];

const categories: Array<"All" | Category> = [
  "All",
  "Academic",
  "Family",
  "Health",
  "Leisure",
];

export default function TasksPage() {
  const [tasks, setTasks] = useState(initialTasks);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<"All" | Category>("All");
  const [status, setStatus] = useState<"All" | Status>("All");
  const [showForm, setShowForm] = useState(false);

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

  function toggleComplete(id: number) {
    setTasks((current) =>
      current.map((task) =>
        task.id === id
          ? {
              ...task,
              status: task.status === "Completed" ? "To Do" : "Completed",
            }
          : task,
      ),
    );
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
              onClick={() => toggleComplete(task.id)}
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

            <button className="task-menu" aria-label={`Options for ${task.title}`}>
              <MoreHorizontal size={18} />
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
        <div className="task-modal-backdrop" onMouseDown={() => setShowForm(false)}>
          <div className="task-modal" onMouseDown={(event) => event.stopPropagation()}>
            <div className="task-modal-heading">
              <div>
                <span>New task</span>
                <h2>Add something to your day</h2>
              </div>
              <button onClick={() => setShowForm(false)}>×</button>
            </div>

            <form
              className="task-form"
              onSubmit={(event) => {
                event.preventDefault();
                setShowForm(false);
              }}
            >
              <label>
                <span>Task name</span>
                <input placeholder="e.g. Study for INF3014F" autoFocus required />
              </label>

              <label>
                <span>Description <small>optional</small></span>
                <textarea placeholder="Add a few details..." rows={3} />
              </label>

              <div className="task-form-row">
                <label>
                  <span>Category</span>
                  <select defaultValue="Academic">
                    <option>Academic</option>
                    <option>Family</option>
                    <option>Health</option>
                    <option>Leisure</option>
                  </select>
                </label>
                <label>
                  <span>Deadline</span>
                  <input type="date" required />
                </label>
              </div>

              <div className="task-form-row">
                <label>
                  <span>Time</span>
                  <input type="time" required />
                </label>
                <label>
                  <span>Duration</span>
                  <select defaultValue="60">
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
                  <select defaultValue="Medium">
                    <option>Low</option>
                    <option>Medium</option>
                    <option>High</option>
                  </select>
                </label>
                <label>
                  <span>Effort</span>
                  <select defaultValue="Medium">
                    <option>Low</option>
                    <option>Medium</option>
                    <option>High</option>
                  </select>
                </label>
              </div>

              <div className="task-form-actions">
                <button type="button" onClick={() => setShowForm(false)}>
                  Cancel
                </button>
                <button type="submit">Add Task</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
