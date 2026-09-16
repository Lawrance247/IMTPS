import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  Clock3,
  Scale,
  Target,
} from "lucide-react";
import { useState } from "react";
import "./SchedulePage.css";

type Strategy = "deadline" | "focus" | "balance";

const strategies = [
  { id: "deadline" as Strategy, name: "Deadline First", icon: Clock3 },
  { id: "focus" as Strategy, name: "Focus", icon: Target },
  { id: "balance" as Strategy, name: "Category Balance", icon: Scale },
];

const schedule = [
  { start: "08:00", end: "09:00", title: "Review lecture notes", category: "Academic" },
  { start: "10:00", end: "11:00", title: "INF3011F reading", category: "Academic" },
  { start: "12:00", end: "13:00", title: "Lunch break", category: "Leisure" },
  { start: "14:00", end: "15:30", title: "Study CSC3003S", category: "Academic" },
  { start: "16:00", end: "17:00", title: "Gym session", category: "Health" },
  { start: "18:30", end: "19:00", title: "Call family", category: "Family" },
];

export default function SchedulePage() {
  const [strategy, setStrategy] = useState<Strategy>("focus");

  return (
    <div className="schedule-page">
      <header className="schedule-header">
        <div>
          <span className="schedule-eyebrow">Your personalised plan</span>
          <h1>Schedule</h1>
          <p>See how your selected strategy shapes the day.</p>
        </div>

        <div className="schedule-date-nav">
          <button aria-label="Previous day"><ChevronLeft size={18} /></button>
          <div>
            <CalendarDays size={17} />
            <span>Thu, 17 Sep 2026</span>
          </div>
          <button aria-label="Next day"><ChevronRight size={18} /></button>
        </div>
      </header>

      <section className="schedule-strategies">
        {strategies.map(({ id, name, icon: Icon }) => (
          <button
            key={id}
            className={strategy === id ? "active" : ""}
            onClick={() => setStrategy(id)}
          >
            <Icon size={18} />
            <span>{name}</span>
          </button>
        ))}
      </section>

      <section className="schedule-layout">
        <article className="day-plan">
          <div className="day-plan-heading">
            <div>
              <span>Thursday</span>
              <h2>Your day</h2>
            </div>
            <span className="strategy-label">
              {strategies.find((item) => item.id === strategy)?.name}
            </span>
          </div>

          <div className="timeline">
            {schedule.map((item) => (
              <div className="timeline-item" key={`${item.start}-${item.title}`}>
                <div className="timeline-time">
                  <strong>{item.start}</strong>
                  <span>{item.end}</span>
                </div>
                <div className="timeline-line">
                  <span className={`timeline-dot ${item.category.toLowerCase()}`} />
                </div>
                <div className={`schedule-event ${item.category.toLowerCase()}`}>
                  <div>
                    <span>{item.category}</span>
                    <h3>{item.title}</h3>
                  </div>
                  <span>{item.start} – {item.end}</span>
                </div>
              </div>
            ))}
          </div>
        </article>

        <aside className="schedule-summary">
          <div className="summary-card">
            <span>Selected strategy</span>
            <div className="summary-strategy-icon">
              {strategy === "deadline" && <Clock3 size={25} />}
              {strategy === "focus" && <Target size={25} />}
              {strategy === "balance" && <Scale size={25} />}
            </div>
            <h3>{strategies.find((item) => item.id === strategy)?.name}</h3>
            <p>
              {strategy === "deadline" &&
                "Tasks with the nearest deadlines are placed earlier in your plan."}
              {strategy === "focus" &&
                "Your selected focus area receives more weight when tasks are prioritised."}
              {strategy === "balance" &&
                "The plan spreads attention across Academic, Family, Health and Leisure."}
            </p>
          </div>

          <div className="summary-card">
            <span>Today's balance</span>
            <div className="balance-list">
              <div><strong>Academic</strong><span>3 tasks</span></div>
              <div><strong>Family</strong><span>1 task</span></div>
              <div><strong>Health</strong><span>1 task</span></div>
              <div><strong>Leisure</strong><span>1 task</span></div>
            </div>
          </div>
        </aside>
      </section>
    </div>
  );
}
