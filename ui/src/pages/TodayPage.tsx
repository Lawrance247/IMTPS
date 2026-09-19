import { API_BASE_URL } from "../config";
import { authHeaders } from "../auth";
import {
  ArrowRight,
  CalendarClock,
  Clock3,
  Crosshair,
  GraduationCap,
  ListChecks,
} from "lucide-react";
import { useState, useEffect } from "react";
import AddTaskModal from "../components/tasks/AddTaskModal";
import { useNavigate } from "react-router-dom";
import "./TodayPage.css";

type ApiTask = {
  id: string;
  status: string;
};

type LoggedInUser = {
  id: string;
  fullName: string;
  email: string;
  createdAt: string;
};

type ScheduleItem = {
  task: {
    id: string;
    title: string;
    category: string;
    durationMinutes: number;
  };
  startTime: string;
  endTime: string;
};

type CurrentSchedule = {
  strategy: string;
  strategyName: string;
  focusCategory: string;
  date: string;
  items: ScheduleItem[];
  totalScheduledMinutes: number;
  deadlineClashes: number;
};

function displayTime(value: string) {
  const timePart = value.includes("T") ? value.split("T")[1] : value;
  return timePart.slice(0, 5);
}

function displayCategory(value: string) {
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

function getGreeting(hour: number) {
  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";
  return "Good evening";
}

export default function TodayPage() {
  const navigate = useNavigate();
  const [showFocusPicker, setShowFocusPicker] = useState(false);
  const [showAddTask, setShowAddTask] = useState(false);
  const [completedTaskIds, setCompletedTaskIds] = useState<Set<string>>(new Set());
  const [currentSchedule, setCurrentSchedule] = useState<CurrentSchedule | null>(null);

  const currentUser = (() => {
    const saved = localStorage.getItem("imtps.currentUser");
    if (!saved) return null;

    try {
      return JSON.parse(saved) as LoggedInUser;
    } catch {
      return null;
    }
  })();

  const today = new Date();
  const todayDate = [
    today.getFullYear(),
    String(today.getMonth() + 1).padStart(2, "0"),
    String(today.getDate()).padStart(2, "0"),
  ].join("-");
  const displayDate = today.toLocaleDateString("en-ZA", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });
  const greeting = getGreeting(today.getHours());
  const firstName = currentUser?.fullName?.trim().split(/\s+/)[0] || "there";

  const fullSchedule =
    currentSchedule?.date === todayDate ? currentSchedule.items : [];

  const schedule = fullSchedule.filter(
    (item) => !completedTaskIds.has(item.task.id),
  );

  useEffect(() => {
    async function loadSavedSchedule() {
      try {
        const response = await fetch(
          `${API_BASE_URL}/schedules/current?date=${todayDate}`,
          { headers: authHeaders() },
        );
        if (response.status === 404) return;
        if (!response.ok) return;
        const saved: CurrentSchedule = await response.json();
        setCurrentSchedule(saved);
      } catch {
        // The rest of Today can still load without a saved schedule.
      }
    }
    loadSavedSchedule();
  }, [todayDate]);

  useEffect(() => {
    async function loadTaskStatuses() {
      try {
        const response = await fetch(`${API_BASE_URL}/tasks`, { headers: authHeaders() });
        if (!response.ok) return;

        const tasks: ApiTask[] = await response.json();
        setCompletedTaskIds(
          new Set(
            tasks
              .filter((task) => task.status === "DONE")
              .map((task) => task.id),
          ),
        );
      } catch {
        // Keep the dashboard usable if the backend is temporarily unavailable.
      }
    }

    loadTaskStatuses();
  }, []);

  const recommendedTask = schedule[0];
  const totalToday = fullSchedule.length;
  const completedToday = fullSchedule.filter((item) =>
    completedTaskIds.has(item.task.id),
  ).length;
  const remainingToday = schedule.length;
  const progressPercent =
    totalToday === 0 ? 0 : Math.round((completedToday / totalToday) * 100);

  return (
    <div className="today-page">
      <header className="today-header">
        <div>
          <span className="today-date">{displayDate}</span>
          <h1>{greeting}, {firstName}</h1>
          <p>Small steps today, big results tomorrow.</p>
        </div>
        <button className="today-add-task" onClick={() => setShowAddTask(true)}>+ Add Task</button>
      </header>

      <section className="today-dashboard">
        <article className="dashboard-card next-task-card">
          <div className="card-heading">
            <div>
              <span>Next recommended task</span>
              <h3>{recommendedTask ? recommendedTask.task.title : "No task scheduled"}</h3>
            </div>
            <GraduationCap size={21} />
          </div>

          {recommendedTask && (
            <>
              <span className={`category-pill ${recommendedTask.task.category.toLowerCase()}`}>
                {displayCategory(recommendedTask.task.category)}
              </span>

              <div className="task-meta">
                <span>
                  <Clock3 size={15} />
                  {displayTime(recommendedTask.startTime)} – {displayTime(recommendedTask.endTime)}
                </span>
                <span>{recommendedTask.task.durationMinutes} min</span>
              </div>
            </>
          )}

          <p>
            {recommendedTask
              ? "Based on your selected strategy, this is the task IMTPS recommends you work on next."
              : "There are no tasks in today's generated schedule."}
          </p>

          {recommendedTask && (
            <button
              className="start-focus"
              onClick={() =>
                navigate("/focus", {
                  state: {
                    task: recommendedTask.task,
                    startTime: recommendedTask.startTime,
                    endTime: recommendedTask.endTime,
                  },
                })
              }
            >
              Start Focus <ArrowRight size={17} />
            </button>
          )}
        </article>

        <article className="dashboard-card progress-card">
          <div className="card-heading">
            <div>
              <span>Today's progress</span>
              <h3>You're moving.</h3>
            </div>
            <ListChecks size={21} />
          </div>

          <div className="progress-ring">
            <div>
              <strong>{completedToday}/{totalToday}</strong>
              <span>completed</span>
            </div>
          </div>

          <div className="progress-caption">
            <span>{progressPercent}% of today's tasks</span>
            <strong>{remainingToday} remaining</strong>
          </div>
        </article>

        <article className="dashboard-card focus-card">
          <div className="card-heading">
            <div>
              <span>Current focus</span>
              <h3>
                {currentSchedule
                  ? displayCategory(currentSchedule.focusCategory)
                  : "No focus selected"}
              </h3>
            </div>
            <Crosshair size={21} />
          </div>

          <div className="focus-symbol">
            <GraduationCap size={42} />
          </div>

          <p>
            {currentSchedule
              ? `${displayCategory(currentSchedule.focusCategory)} is the focus category for your currently selected schedule.`
              : "Choose a schedule with a focus category to see it here."}
          </p>
          <button className="secondary-action" onClick={() => setShowFocusPicker(true)}>Change Focus</button>
        </article>

        <article className="dashboard-card schedule-card">
          <div className="card-heading">
            <div>
              <span>Today's schedule</span>
              <h3>Your day at a glance</h3>
              {currentSchedule?.date === todayDate && (
                <small>Currently using: {currentSchedule.strategyName}</small>
              )}
            </div>
            <CalendarClock size={21} />
          </div>

          <div className="schedule-list">
            {schedule.map((item) => (
              <div className="schedule-row" key={`${item.startTime}-${item.task.id}`}>
                <time>{displayTime(item.startTime)}</time>
                <span className={`schedule-dot ${item.task.category.toLowerCase()}`} />
                <strong>{item.task.title}</strong>
              </div>
            ))}

            {schedule.length === 0 && (
              <p>No schedule has been selected for today yet.</p>
            )}
          </div>

          <button className="schedule-link" onClick={() => navigate("/schedule")}>
            View full schedule <ArrowRight size={16} />
          </button>
        </article>
      </section>
      {showAddTask && (
        <AddTaskModal onClose={() => setShowAddTask(false)} />
      )}

      {showFocusPicker && (
        <div className="task-modal-backdrop" onMouseDown={() => setShowFocusPicker(false)}>
          <div className="task-modal" onMouseDown={(event) => event.stopPropagation()}>
            <div className="task-modal-heading">
              <div>
                <span>Current focus</span>
                <h2>Change focus category</h2>
              </div>
              <button onClick={() => setShowFocusPicker(false)}>×</button>
            </div>

            <div className="task-form">
              <label>
                <span>Focus category</span>
                <select
                  defaultValue={currentSchedule?.focusCategory ?? "ACADEMIC"}
                  onChange={async (event) => {
                    if (!currentSchedule) return;

                    const focusCategory = event.target.value;
                    const previousSchedule = currentSchedule;

                    setCurrentSchedule({ ...currentSchedule, focusCategory });
                    setShowFocusPicker(false);

                    try {
                      const response = await fetch(
                        `${API_BASE_URL}/schedules/current?date=${currentSchedule.date}`,
                        {
                          method: "PUT",
                          headers: authHeaders(true),
                          body: JSON.stringify({ focusCategory }),
                        },
                      );

                      if (!response.ok) throw new Error("Could not save focus category");
                    } catch {
                      setCurrentSchedule(previousSchedule);
                    }
                  }}
                >
                  <option value="ACADEMIC">Academic</option>
                  <option value="FAMILY">Family</option>
                  <option value="HEALTH">Health</option>
                  <option value="LEISURE">Leisure</option>
                </select>
              </label>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
