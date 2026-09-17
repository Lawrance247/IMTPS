import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  Clock3,
  Scale,
  Target,
} from "lucide-react";
import { useEffect, useRef, useState } from "react";
import "./SchedulePage.css";

type Strategy = "deadline" | "focus" | "slack" | "balance";
type FocusCategory = "ACADEMIC" | "FAMILY" | "HEALTH" | "LEISURE";

const strategies = [
  { id: "deadline" as Strategy, name: "Earliest Deadline First", icon: Clock3 },
  { id: "focus" as Strategy, name: "Weighted Focus Mode", icon: Target },
  { id: "slack" as Strategy, name: "Slack Focus", icon: Target },
  { id: "balance" as Strategy, name: "Category Balance", icon: Scale },
];

type ScheduleItem = {
  task: {
    id: string;
    title: string;
    category: string;
  };
  startTime: string;
  endTime: string;
  deadlineClash: boolean;
};

type ScheduleResponse = {
  strategy: string;
  items: ScheduleItem[];
  totalScheduledMinutes: number;
  deadlineClashes: number;
};

function timeOnly(value: string) {
  return value.includes("T") ? value.split("T")[1].slice(0, 5) : value.slice(0, 5);
}

function categoryLabel(value: string) {
  return value.charAt(0) + value.slice(1).toLowerCase();
}

export default function SchedulePage() {
  const [strategy, setStrategy] = useState<Strategy>("focus");
  const [focusCategory, setFocusCategory] = useState<FocusCategory>("ACADEMIC");
  const dateInputRef = useRef<HTMLInputElement>(null);
  const [selectedDate, setSelectedDate] = useState(new Date(2026, 8, 17));
  const [schedule, setSchedule] = useState<ScheduleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    generateSchedule();
  }, [strategy, selectedDate, focusCategory]);

  function changeDay(days: number) {
    setSelectedDate((current) => {
      const next = new Date(current);
      next.setDate(next.getDate() + days);
      return next;
    });
  }

  function dateForApi(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  function dateLabel(date: Date) {
    return date.toLocaleDateString("en-ZA", {
      weekday: "short",
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  }

  function weekdayLabel(date: Date) {
    return date.toLocaleDateString("en-ZA", { weekday: "long" });
  }

  async function generateSchedule() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch("http://localhost:8080/api/schedules/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          strategy,
          focusCategory,
          dayStart: "08:00",
          dayEnd: "20:00",
          scheduleStart: `${dateForApi(selectedDate)}T08:00:00`,
        }),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => null);
        throw new Error(body?.error ?? "Could not generate schedule");
      }

      const data: ScheduleResponse = await response.json();
      setSchedule(data.items);

      const selectedPlan = {
        strategy,
        strategyName: strategies.find((item) => item.id === strategy)?.name ?? data.strategy,
        focusCategory,
        date: dateForApi(selectedDate),
        items: data.items,
        totalScheduledMinutes: data.totalScheduledMinutes,
        deadlineClashes: data.deadlineClashes,
      };

      localStorage.setItem("imtps.currentSchedule", JSON.stringify(selectedPlan));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not generate schedule");
    } finally {
      setLoading(false);
    }
  }

  const categoryCounts = schedule.reduce(
    (counts, item) => {
      const category = item.task.category.toUpperCase();

      if (category in counts) {
        counts[category as keyof typeof counts] += 1;
      }

      return counts;
    },
    { ACADEMIC: 0, FAMILY: 0, HEALTH: 0, LEISURE: 0 }
  );

  function taskCountLabel(count: number) {
    return `${count} ${count === 1 ? "task" : "tasks"}`;
  }

  return (
    <div className="schedule-page">
      <header className="schedule-header">
        <div>
          <span className="schedule-eyebrow">Your personalised plan</span>
          <h1>Schedule</h1>
          <p>See how your selected strategy shapes the day.</p>
        </div>

        <div className="schedule-date-nav">
          <button aria-label="Previous day" onClick={() => changeDay(-1)}><ChevronLeft size={18} /></button>
          <div>
            <button
              type="button"
              className="schedule-calendar-button"
              aria-label="Choose date"
              onClick={() => {
                const input = dateInputRef.current;
                if (!input) return;

                if ("showPicker" in input) {
                  input.showPicker();
                } else {
                  input.click();
                }
              }}
            >
              <CalendarDays size={17} />
            </button>
            <span>{dateLabel(selectedDate)}</span>
            <input
              ref={dateInputRef}
              className="schedule-date-picker"
              type="date"
              value={dateForApi(selectedDate)}
              onChange={(event) => {
                const [year, month, day] = event.target.value
                  .split("-")
                  .map(Number);

                if (year && month && day) {
                  setSelectedDate(new Date(year, month - 1, day));
                }
              }}
              aria-label="Select schedule date"
            />
          </div>
          <button aria-label="Next day" onClick={() => changeDay(1)}><ChevronRight size={18} /></button>
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

      {(strategy === "focus" || strategy === "slack") && (
        <section className="schedule-focus-selector">
          <label htmlFor="focus-category">Focus category</label>
          <select
            id="focus-category"
            value={focusCategory}
            onChange={(event) =>
              setFocusCategory(event.target.value as FocusCategory)
            }
          >
            <option value="ACADEMIC">Academic</option>
            <option value="FAMILY">Family</option>
            <option value="HEALTH">Health</option>
            <option value="LEISURE">Leisure</option>
          </select>
        </section>
      )}

      <section className="schedule-layout">
        <article className="day-plan">
          <div className="day-plan-heading">
            <div>
              <span>{weekdayLabel(selectedDate)}</span>
              <h2>Your day</h2>
            </div>
            <span className="strategy-label">
              {strategies.find((item) => item.id === strategy)?.name}
            </span>
          </div>

          <div className="timeline">
            {loading && <p>Generating schedule...</p>}
            {error && <p>{error}</p>}

            {!loading && !error && schedule.map((item) => {
              const category = categoryLabel(item.task.category);
              const start = timeOnly(item.startTime);
              const end = timeOnly(item.endTime);

              return (
                <div className="timeline-item" key={`${item.task.id}-${item.startTime}`}>
                  <div className="timeline-time">
                    <strong>{start}</strong>
                    <span>{end}</span>
                  </div>
                  <div className="timeline-line">
                    <span className={`timeline-dot ${category.toLowerCase()}`} />
                  </div>
                  <div className={`schedule-event ${category.toLowerCase()}`}>
                    <div>
                      <span>{category}</span>
                      <h3>{item.task.title}</h3>
                    </div>
                    <span>{start} – {end}</span>
                  </div>
                </div>
              );
            })}
          </div>
        </article>

        <aside className="schedule-summary">
          <div className="summary-card">
            <span>Selected strategy</span>
            <div className="summary-strategy-icon">
              {strategy === "deadline" && <Clock3 size={25} />}
              {strategy === "focus" && <Target size={25} />}
              {strategy === "slack" && <Target size={25} />}
              {strategy === "balance" && <Scale size={25} />}
            </div>
            <h3>{strategies.find((item) => item.id === strategy)?.name}</h3>
            <p>
              {strategy === "deadline" &&
                "Tasks with the nearest deadlines are placed earlier in your plan."}
              {strategy === "focus" &&
                "Your selected focus area receives more weight when tasks are prioritised."}
              {strategy === "slack" &&
                "Tasks are prioritised using their available slack together with your selected focus area."}
              {strategy === "balance" &&
                "The plan spreads attention across Academic, Family, Health and Leisure."}
            </p>
          </div>

          <div className="summary-card">
            <span>Today's balance</span>
            <div className="balance-list">
              <div><strong>Academic</strong><span>{taskCountLabel(categoryCounts.ACADEMIC)}</span></div>
              <div><strong>Family</strong><span>{taskCountLabel(categoryCounts.FAMILY)}</span></div>
              <div><strong>Health</strong><span>{taskCountLabel(categoryCounts.HEALTH)}</span></div>
              <div><strong>Leisure</strong><span>{taskCountLabel(categoryCounts.LEISURE)}</span></div>
            </div>
          </div>
        </aside>
      </section>
    </div>
  );
}
