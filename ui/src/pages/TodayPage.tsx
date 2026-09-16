import {
  ArrowRight,
  Scale,
  CalendarClock,
  CheckCircle2,
  Clock3,
  Crosshair,
  GraduationCap,
  ListChecks,
  Target,
} from "lucide-react";
import { useState } from "react";
import "./TodayPage.css";

type Strategy = "deadline" | "focus" | "balance";

const strategies = [
  {
    id: "deadline" as Strategy,
    name: "Deadline First",
    detail: "Handle what's due soon",
    icon: CalendarClock,
  },
  {
    id: "focus" as Strategy,
    name: "Focus",
    detail: "Prioritise what matters",
    icon: Target,
  },
  {
    id: "balance" as Strategy,
    name: "Category Balance",
    detail: "Balance your life areas",
    icon: Scale,
  },
];

const schedule = [
  { time: "08:00", title: "Review lecture notes", category: "academic" },
  { time: "10:00", title: "INF3011F reading", category: "academic" },
  { time: "12:00", title: "Lunch break", category: "leisure" },
  { time: "14:00", title: "Study CSC3003S", category: "academic" },
  { time: "16:00", title: "Gym session", category: "health" },
  { time: "18:30", title: "Call family", category: "family" },
];

export default function TodayPage() {
  const [strategy, setStrategy] = useState<Strategy>("focus");

  return (
    <div className="today-page">
      <header className="today-header">
        <div>
          <span className="today-date">Thursday, 17 September</span>
          <h1>Good afternoon, Lawrence <span aria-hidden="true">👋</span></h1>
          <p>Small steps today, big results tomorrow.</p>
        </div>
        <button className="today-add-task">+ Add Task</button>
      </header>

      <section className="strategy-section">
        <div className="section-heading">
          <div>
            <span>Planning strategy</span>
            <h2>How should IMTPS plan for you?</h2>
          </div>
          <p>You can switch at any time.</p>
        </div>

        <div className="strategy-grid">
          {strategies.map(({ id, name, detail, icon: Icon }) => (
            <button
              key={id}
              className={`strategy-card ${strategy === id ? "selected" : ""}`}
              onClick={() => setStrategy(id)}
            >
              <div className="strategy-icon"><Icon size={20} /></div>
              <div>
                <strong>{name}</strong>
                <span>{detail}</span>
              </div>
              {strategy === id && <CheckCircle2 className="strategy-check" size={18} />}
            </button>
          ))}
        </div>
      </section>

      <section className="today-dashboard">
        <article className="dashboard-card next-task-card">
          <div className="card-heading">
            <div>
              <span>Next recommended task</span>
              <h3>Study CSC3003S</h3>
            </div>
            <GraduationCap size={21} />
          </div>

          <span className="category-pill academic">Academic</span>

          <div className="task-meta">
            <span><Clock3 size={15} /> 14:00 – 15:30</span>
            <span>90 min</span>
          </div>

          <p>
            Based on your selected strategy, this is the task IMTPS recommends
            you work on next.
          </p>

          <button className="start-focus">
            Start Focus <ArrowRight size={17} />
          </button>
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
              <strong>3/6</strong>
              <span>completed</span>
            </div>
          </div>

          <div className="progress-caption">
            <span>50% of today's tasks</span>
            <strong>3 remaining</strong>
          </div>
        </article>

        <article className="dashboard-card focus-card">
          <div className="card-heading">
            <div>
              <span>Current focus</span>
              <h3>Academic</h3>
            </div>
            <Crosshair size={21} />
          </div>

          <div className="focus-symbol">
            <GraduationCap size={42} />
          </div>

          <p>Academic tasks currently receive more attention in weighted planning.</p>
          <button className="secondary-action">Change Focus</button>
        </article>

        <article className="dashboard-card schedule-card">
          <div className="card-heading">
            <div>
              <span>Today's schedule</span>
              <h3>Your day at a glance</h3>
            </div>
            <CalendarClock size={21} />
          </div>

          <div className="schedule-list">
            {schedule.map((item) => (
              <div className="schedule-row" key={`${item.time}-${item.title}`}>
                <time>{item.time}</time>
                <span className={`schedule-dot ${item.category}`} />
                <strong>{item.title}</strong>
              </div>
            ))}
          </div>

          <button className="schedule-link">
            View full schedule <ArrowRight size={16} />
          </button>
        </article>
      </section>
    </div>
  );
}
