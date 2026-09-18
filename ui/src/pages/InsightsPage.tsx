import { API_BASE_URL } from "../config";
import { authHeaders } from "../auth";
import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  Clock3,
  Scale,
  Target,
  TrendingUp,
} from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import "./InsightsPage.css";

type ApiTask = {
  id: string;
  title: string;
  category: string;
  deadline: string;
  durationMinutes: number;
  status: string;
};

type SavedSchedule = {
  strategy: string;
  strategyName: string;
  focusCategory: string;
  date: string;
  totalScheduledMinutes: number;
  deadlineClashes: number;
  items: {
    task: {
      id: string;
      title: string;
      category: string;
      durationMinutes: number;
    };
    startTime: string;
    endTime: string;
    deadlineClash: boolean;
  }[];
};

const categoryNames = ["ACADEMIC", "FAMILY", "HEALTH", "LEISURE"] as const;

function categoryLabel(value: string) {
  return value.charAt(0) + value.slice(1).toLowerCase();
}

function formatMinutes(minutes: number) {
  const hours = Math.floor(minutes / 60);
  const remaining = minutes % 60;
  if (hours === 0) return `${remaining}m`;
  if (remaining === 0) return `${hours}h`;
  return `${hours}h ${remaining}m`;
}

export default function InsightsPage() {
  const [tasks, setTasks] = useState<ApiTask[]>([]);
  const [schedule, setSchedule] = useState<SavedSchedule | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadInsights() {
      try {
        setLoading(true);
        setError("");

        const [tasksResponse, scheduleResponse] = await Promise.all([
          fetch(`${API_BASE_URL}/tasks`, { headers: authHeaders() }),
          fetch(`${API_BASE_URL}/schedules/current`, { headers: authHeaders() }),
        ]);

        if (!tasksResponse.ok) throw new Error("Could not load your task insights.");
        setTasks(await tasksResponse.json());

        if (scheduleResponse.ok) {
          setSchedule(await scheduleResponse.json());
        } else if (scheduleResponse.status === 404) {
          setSchedule(null);
        } else {
          throw new Error("Could not load your saved schedule.");
        }
      } catch (requestError) {
        setError(
          requestError instanceof Error
            ? requestError.message
            : "Could not load insights.",
        );
      } finally {
        setLoading(false);
      }
    }

    loadInsights();
  }, []);

  const completedTasks = useMemo(
    () => tasks.filter((task) => task.status === "DONE"),
    [tasks],
  );

  const completionRate =
    tasks.length === 0 ? 0 : Math.round((completedTasks.length / tasks.length) * 100);

  const overdueTasks = useMemo(() => {
    const now = new Date();
    return tasks.filter(
      (task) => task.status !== "DONE" && new Date(task.deadline) < now,
    ).length;
  }, [tasks]);

  const categoryStats = useMemo(() => {
    const source = schedule?.items ?? [];
    const totalMinutes = source.reduce(
      (sum, item) => sum + item.task.durationMinutes,
      0,
    );

    return categoryNames.map((name) => {
      const minutes = source
        .filter((item) => item.task.category.toUpperCase() === name)
        .reduce((sum, item) => sum + item.task.durationMinutes, 0);

      return {
        name: categoryLabel(name),
        minutes,
        value: totalMinutes === 0 ? 0 : Math.round((minutes / totalMinutes) * 100),
        className: name.toLowerCase(),
      };
    });
  }, [schedule]);

  const mostActive = categoryStats.reduce(
    (best, current) => (current.minutes > best.minutes ? current : best),
    categoryStats[0],
  );

  const scheduledTaskCount = schedule?.items.length ?? 0;
  const plannedMinutes = schedule?.totalScheduledMinutes ?? 0;

  return (
    <div className="insights-page">
      <header className="insights-header">
        <div>
          <span className="insights-eyebrow">Understand your plan</span>
          <h1>Insights</h1>
          <p>See real information from your tasks and latest saved schedule.</p>
        </div>
        <div className="insights-period">
          {schedule ? `Schedule: ${schedule.date}` : "No saved schedule"}
        </div>
      </header>

      {loading && <p>Loading insights...</p>}
      {error && <p className="insights-error">{error}</p>}

      {!loading && !error && (
        <>
          <section className="metric-grid">
            <article className="metric-card">
              <div className="metric-icon"><CheckCircle2 size={20} /></div>
              <span>Tasks completed</span>
              <strong>{completedTasks.length}</strong>
              <small>of {tasks.length} tasks</small>
            </article>

            <article className="metric-card">
              <div className="metric-icon"><Clock3 size={20} /></div>
              <span>Planned time</span>
              <strong>{formatMinutes(plannedMinutes)}</strong>
              <small>{scheduledTaskCount} scheduled {scheduledTaskCount === 1 ? "task" : "tasks"}</small>
            </article>

            <article className="metric-card">
              <div className="metric-icon"><TrendingUp size={20} /></div>
              <span>Completion rate</span>
              <strong>{completionRate}%</strong>
              <small>from your current tasks</small>
            </article>

            <article className="metric-card">
              <div className="metric-icon"><Target size={20} /></div>
              <span>Largest planned category</span>
              <strong>{plannedMinutes > 0 ? mostActive.name : "—"}</strong>
              <small>{plannedMinutes > 0 ? `${mostActive.value}% of planned time` : "Generate a schedule first"}</small>
            </article>
          </section>

          <section className="insights-grid">
            <article className="insight-card weekly-card">
              <div className="insight-heading">
                <div>
                  <span>Task progress</span>
                  <h2>Current workload</h2>
                </div>
                <Activity size={20} />
              </div>

              <div className="strategy-usage">
                <div>
                  <span>Completed</span>
                  <div><i style={{ width: `${completionRate}%` }} /></div>
                  <strong>{completedTasks.length}</strong>
                </div>
                <div>
                  <span>Remaining</span>
                  <div><i style={{ width: `${tasks.length ? ((tasks.length - completedTasks.length) / tasks.length) * 100 : 0}%` }} /></div>
                  <strong>{tasks.length - completedTasks.length}</strong>
                </div>
                <div>
                  <span>Overdue</span>
                  <div><i style={{ width: `${tasks.length ? (overdueTasks / tasks.length) * 100 : 0}%` }} /></div>
                  <strong>{overdueTasks}</strong>
                </div>
              </div>
            </article>

            <article className="insight-card balance-card">
              <div className="insight-heading">
                <div>
                  <span>Life balance</span>
                  <h2>Planned time by category</h2>
                </div>
                <Scale size={20} />
              </div>

              <div className="balance-summary">
                <strong>{formatMinutes(plannedMinutes)}</strong>
                <span>planned in latest schedule</span>
              </div>

              <div className="category-legend">
                {categoryStats.map((item) => (
                  <div key={item.name}>
                    <span className={`legend-dot ${item.className}`} />
                    <strong>{item.name}</strong>
                    <span>{item.value}% · {formatMinutes(item.minutes)}</span>
                  </div>
                ))}
              </div>
            </article>

            <article className="insight-card strategy-insight">
              <div className="insight-heading">
                <div>
                  <span>Saved plan</span>
                  <h2>Scheduling information</h2>
                </div>
                <Target size={20} />
              </div>

              <div className="observation">
                <span className="observation-mark">01</span>
                <p>{schedule ? `Latest strategy: ${schedule.strategyName}.` : "No schedule has been generated yet."}</p>
              </div>
              <div className="observation">
                <span className="observation-mark">02</span>
                <p>{schedule ? `Focus category: ${categoryLabel(schedule.focusCategory)}.` : "Choose a strategy and generate a schedule to see planning information."}</p>
              </div>
              <div className="observation">
                <span className="observation-mark">03</span>
                <p>{schedule ? `${schedule.deadlineClashes} deadline ${schedule.deadlineClashes === 1 ? "clash" : "clashes"} in the saved schedule.` : "Deadline clashes will appear here once a schedule is saved."}</p>
              </div>
            </article>

            <article className="insight-card observation-card">
              <div className="insight-heading">
                <div>
                  <span>Current picture</span>
                  <h2>What your data shows</h2>
                </div>
                <AlertTriangle size={20} />
              </div>

              <div className="observation">
                <span className="observation-mark">01</span>
                <p>{tasks.length === 0 ? "You do not have any tasks yet." : `${completedTasks.length} of your ${tasks.length} tasks are completed.`}</p>
              </div>
              <div className="observation">
                <span className="observation-mark">02</span>
                <p>{overdueTasks === 0 ? "You currently have no overdue unfinished tasks." : `${overdueTasks} unfinished ${overdueTasks === 1 ? "task is" : "tasks are"} overdue.`}</p>
              </div>
              <div className="observation">
                <span className="observation-mark">03</span>
                <p>{plannedMinutes === 0 ? "Generate a schedule to see how your planned time is distributed." : `${mostActive.name} has the largest share of your latest schedule at ${mostActive.value}%.`}</p>
              </div>
            </article>
          </section>
        </>
      )}
    </div>
  );
}
