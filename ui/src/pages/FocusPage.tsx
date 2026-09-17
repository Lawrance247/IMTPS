import {
  Check,
  Clock3,
  GraduationCap,
  Pause,
  Play,
  RotateCcw,
  Square,
} from "lucide-react";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./FocusPage.css";

type FocusTask = {
  id: string;
  title: string;
  description: string;
  category: string;
  deadline?: string;
  durationMinutes: number;
  importance?: string;
  effort?: string;
  status?: string;
};

type ScheduledItem = {
  task: FocusTask;
  startTime: string;
  endTime: string;
};

type FocusLocationState = {
  task?: FocusTask;
};

function displayCategory(value: string) {
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

function formatDuration(totalSeconds: number) {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  return [hours, minutes, seconds]
    .map((value) => String(value).padStart(2, "0"))
    .join(":");
}

export default function FocusPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const routeTask = (location.state as FocusLocationState | null)?.task;
  const [selectedTask, setSelectedTask] = useState<FocusTask | undefined>(routeTask);
  const [availableTasks, setAvailableTasks] = useState<FocusTask[]>([]);
  const [taskLoadError, setTaskLoadError] = useState("");
  const totalSeconds = Math.max((selectedTask?.durationMinutes ?? 60) * 60, 60);

  const [secondsLeft, setSecondsLeft] = useState(totalSeconds);
  const [running, setRunning] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [nextTask, setNextTask] = useState<ScheduledItem | null>(null);
  const [completeError, setCompleteError] = useState("");

  useEffect(() => {
    async function loadAvailableTasks() {
      setTaskLoadError("");

      try {
        const response = await fetch("http://localhost:8080/api/tasks");
        if (!response.ok) throw new Error("Could not load tasks");

        const tasks: FocusTask[] = await response.json();
        setAvailableTasks(tasks.filter((task) => task.status !== "DONE"));
      } catch (error) {
        setTaskLoadError(
          error instanceof Error ? error.message : "Could not load tasks",
        );
      }
    }

    loadAvailableTasks();
  }, []);

  useEffect(() => {
    setSecondsLeft(totalSeconds);
    setRunning(false);
    setCompleted(false);
  }, [totalSeconds, selectedTask?.id]);

  useEffect(() => {
    const saved = localStorage.getItem("imtps.currentSchedule");
    if (!saved || !selectedTask) {
      setNextTask(null);
      return;
    }

    try {
      const plan = JSON.parse(saved) as { items?: ScheduledItem[] };
      const items = plan.items ?? [];
      const currentIndex = items.findIndex((item) => item.task.id === selectedTask.id);
      setNextTask(currentIndex >= 0 ? items[currentIndex + 1] ?? null : null);
    } catch {
      setNextTask(null);
    }
  }, [selectedTask?.id]);

  useEffect(() => {
    if (!running || secondsLeft <= 0) return;

    const timer = window.setInterval(() => {
      setSecondsLeft((current) => {
        if (current <= 1) {
          setRunning(false);
          return 0;
        }
        return current - 1;
      });
    }, 1000);

    return () => window.clearInterval(timer);
  }, [running, secondsLeft]);

  const minutes = Math.floor(secondsLeft / 60);
  const seconds = secondsLeft % 60;
  const progress = ((totalSeconds - secondsLeft) / totalSeconds) * 100;

  function resetTimer() {
    setSecondsLeft(totalSeconds);
    setRunning(false);
    setCompleted(false);
  }

  async function finishTask() {
    if (!selectedTask) return;

    setRunning(false);
    setCompleteError("");

    try {
      const response = await fetch(`http://localhost:8080/api/tasks/${selectedTask.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...selectedTask,
          status: "DONE",
        }),
      });

      if (!response.ok) {
        throw new Error("Could not mark task as complete");
      }

      setCompleted(true);
      setAvailableTasks((current) =>
        current.filter((task) => task.id !== selectedTask.id),
      );
    } catch (error) {
      setCompleteError(
        error instanceof Error ? error.message : "Could not mark task as complete",
      );
    }
  }

  return (
    <div className="focus-page">
      <header className="focus-header">
        <div>
          <span className="focus-eyebrow">Distraction-free session</span>
          <h1>Focus Mode</h1>
          <p>One task. One session. Give it your attention.</p>
        </div>
        <button className="focus-exit" onClick={() => navigate("/today")}>
          <Square size={15} />
          Exit Focus
        </button>
      </header>

      <section className="focus-workspace">
        <article className="focus-timer-card">
          <div className="focus-category">
            <GraduationCap size={17} />
            {selectedTask ? displayCategory(selectedTask.category) : "No task selected"}
          </div>

          <label className="focus-task-picker">
            <span>Select task</span>
            <select
              value={selectedTask?.id ?? ""}
              onChange={(event) => {
                const task = availableTasks.find(
                  (item) => item.id === event.target.value,
                );
                setSelectedTask(task);
                setCompleteError("");
              }}
            >
              <option value="">Select a task</option>
              {availableTasks.map((task) => (
                <option key={task.id} value={task.id}>
                  {task.title}
                </option>
              ))}
            </select>
          </label>
          {taskLoadError && (
            <p className="focus-task-description">{taskLoadError}</p>
          )}

          <span className="focus-current-label">Current task</span>
          <h2>{selectedTask?.title ?? "Choose a task to start focusing"}</h2>
          <p className="focus-task-description">
            {selectedTask?.description || "Select any unfinished task above to start a focus session."}
          </p>

          <div
            className="focus-ring"
            style={{
              background: `conic-gradient(var(--primary) ${progress}%, var(--border) ${progress}% 100%)`,
            }}
          >
            <div className="focus-ring-inner">
              <span>Time remaining</span>
              <strong>
                {String(minutes).padStart(2, "0")}:
                {String(seconds).padStart(2, "0")}
              </strong>
              <small>of {formatDuration(totalSeconds)}</small>
            </div>
          </div>

          <div className="focus-controls">
            <button
              className="focus-reset"
              onClick={resetTimer}
              aria-label="Reset timer"
            >
              <RotateCcw size={19} />
            </button>

            <button
              className="focus-play"
              onClick={() => setRunning((current) => !current)}
              disabled={secondsLeft === 0 || completed}
            >
              {running ? <Pause size={22} /> : <Play size={22} />}
              {running ? "Pause" : "Start"}
            </button>

            <button
              className="focus-reset"
              onClick={() => setRunning(false)}
              aria-label="Stop timer"
            >
              <Square size={18} />
            </button>
          </div>

          <button
            className={`focus-complete ${completed ? "done" : ""}`}
            onClick={finishTask}
            disabled={!selectedTask || completed}
          >
            <Check size={18} />
            {completed ? "Task Completed" : "Mark Complete"}
          </button>
          {completeError && <p className="focus-task-description">{completeError}</p>}
        </article>

        <aside className="focus-side">
          <article className="focus-info-card">
            <span>Session</span>
            <h3>{selectedTask ? `${selectedTask.durationMinutes} minutes` : "No active session"}</h3>
            <div className="focus-stat">
              <Clock3 size={17} />
              <div>
                <strong>{Math.round(progress)}%</strong>
                <small>session progress</small>
              </div>
            </div>
          </article>

          <article className="focus-info-card next-up-card">
            <span>Next up</span>
            <div className="next-up-icon">
              <GraduationCap size={21} />
            </div>
            {nextTask ? (
              <>
                <h3>{nextTask.task.title}</h3>
                <p>
                  {nextTask.startTime.slice(11, 16)} – {nextTask.endTime.slice(11, 16)}
                  {" · "}{nextTask.task.durationMinutes} min
                </p>
              </>
            ) : (
              <>
                <h3>No task next</h3>
                <p>Your current schedule has no task after this one.</p>
              </>
            )}
          </article>

          <article className="focus-message">
            <strong>Stay with the task in front of you.</strong>
            <p>The rest of your plan will still be here when you're done.</p>
          </article>
        </aside>
      </section>
    </div>
  );
}
