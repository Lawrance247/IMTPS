import {
  Check,
  ChevronRight,
  Clock3,
  GraduationCap,
  Pause,
  Play,
  RotateCcw,
  Square,
} from "lucide-react";
import { useEffect, useState } from "react";
import "./FocusPage.css";

const TOTAL_SECONDS = 90 * 60;

export default function FocusPage() {
  const [secondsLeft, setSecondsLeft] = useState(45 * 60 + 12);
  const [running, setRunning] = useState(false);
  const [completed, setCompleted] = useState(false);

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
  const progress = ((TOTAL_SECONDS - secondsLeft) / TOTAL_SECONDS) * 100;

  function resetTimer() {
    setSecondsLeft(TOTAL_SECONDS);
    setRunning(false);
    setCompleted(false);
  }

  function finishTask() {
    setRunning(false);
    setCompleted(true);
  }

  return (
    <div className="focus-page">
      <header className="focus-header">
        <div>
          <span className="focus-eyebrow">Distraction-free session</span>
          <h1>Focus Mode</h1>
          <p>One task. One session. Give it your attention.</p>
        </div>
        <button className="focus-exit" onClick={() => setRunning(false)}>
          <Square size={15} />
          Exit Focus
        </button>
      </header>

      <section className="focus-workspace">
        <article className="focus-timer-card">
          <div className="focus-category">
            <GraduationCap size={17} />
            Academic
          </div>

          <span className="focus-current-label">Current task</span>
          <h2>Study CSC3003S</h2>
          <p className="focus-task-description">
            Review the TOA material and complete the practice questions.
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
              <small>of 01:30:00</small>
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
          >
            <Check size={18} />
            {completed ? "Task Completed" : "Mark Complete"}
          </button>
        </article>

        <aside className="focus-side">
          <article className="focus-info-card">
            <span>Session</span>
            <h3>90 minutes</h3>
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
            <h3>INF3011F reading</h3>
            <p>10:00 – 11:00 · 60 min</p>
            <button>
              View task <ChevronRight size={16} />
            </button>
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
