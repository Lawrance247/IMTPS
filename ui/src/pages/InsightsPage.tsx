import {
  Activity,
  CheckCircle2,
  Clock3,
  Scale,
  Target,
  TrendingUp,
} from "lucide-react";
import "./InsightsPage.css";

const days = [
  { day: "Mon", value: 62 },
  { day: "Tue", value: 78 },
  { day: "Wed", value: 55 },
  { day: "Thu", value: 84 },
  { day: "Fri", value: 70 },
  { day: "Sat", value: 42 },
  { day: "Sun", value: 58 },
];

const categories = [
  { name: "Academic", value: 46, className: "academic" },
  { name: "Family", value: 18, className: "family" },
  { name: "Health", value: 21, className: "health" },
  { name: "Leisure", value: 15, className: "leisure" },
];

export default function InsightsPage() {
  return (
    <div className="insights-page">
      <header className="insights-header">
        <div>
          <span className="insights-eyebrow">Understand your habits</span>
          <h1>Insights</h1>
          <p>See how your time, tasks and life categories are working together.</p>
        </div>
        <div className="insights-period">This week</div>
      </header>

      <section className="metric-grid">
        <article className="metric-card">
          <div className="metric-icon"><CheckCircle2 size={20} /></div>
          <span>Tasks completed</span>
          <strong>18</strong>
          <small>of 24 planned</small>
        </article>

        <article className="metric-card">
          <div className="metric-icon"><Clock3 size={20} /></div>
          <span>Focus time</span>
          <strong>9h 40m</strong>
          <small>across 8 sessions</small>
        </article>

        <article className="metric-card">
          <div className="metric-icon"><TrendingUp size={20} /></div>
          <span>Completion rate</span>
          <strong>75%</strong>
          <small>+8% from last week</small>
        </article>

        <article className="metric-card">
          <div className="metric-icon"><Target size={20} /></div>
          <span>Most active</span>
          <strong>Academic</strong>
          <small>46% of planned time</small>
        </article>
      </section>

      <section className="insights-grid">
        <article className="insight-card weekly-card">
          <div className="insight-heading">
            <div>
              <span>Weekly activity</span>
              <h2>Task completion</h2>
            </div>
            <Activity size={20} />
          </div>

          <div className="bar-chart">
            {days.map((item) => (
              <div className="bar-column" key={item.day}>
                <div className="bar-track">
                  <div className="bar-fill" style={{ height: `${item.value}%` }} />
                </div>
                <span>{item.day}</span>
              </div>
            ))}
          </div>
        </article>

        <article className="insight-card balance-card">
          <div className="insight-heading">
            <div>
              <span>Life balance</span>
              <h2>Time by category</h2>
            </div>
            <Scale size={20} />
          </div>

          <div className="balance-donut">
            <div>
              <strong>24h</strong>
              <span>planned</span>
            </div>
          </div>

          <div className="category-legend">
            {categories.map((item) => (
              <div key={item.name}>
                <span className={`legend-dot ${item.className}`} />
                <strong>{item.name}</strong>
                <span>{item.value}%</span>
              </div>
            ))}
          </div>
        </article>

        <article className="insight-card strategy-insight">
          <div className="insight-heading">
            <div>
              <span>Strategy insight</span>
              <h2>How you've been planning</h2>
            </div>
            <Target size={20} />
          </div>

          <div className="strategy-usage">
            <div>
              <span>Focus</span>
              <div><i style={{ width: "58%" }} /></div>
              <strong>58%</strong>
            </div>
            <div>
              <span>Deadline First</span>
              <div><i style={{ width: "27%" }} /></div>
              <strong>27%</strong>
            </div>
            <div>
              <span>Category Balance</span>
              <div><i style={{ width: "15%" }} /></div>
              <strong>15%</strong>
            </div>
          </div>
        </article>

        <article className="insight-card observation-card">
          <div className="insight-heading">
            <div>
              <span>Pattern noticed</span>
              <h2>Your week at a glance</h2>
            </div>
            <TrendingUp size={20} />
          </div>

          <div className="observation">
            <span className="observation-mark">01</span>
            <p>You complete more planned work during weekdays than weekends.</p>
          </div>
          <div className="observation">
            <span className="observation-mark">02</span>
            <p>Academic work currently takes the largest share of your planned time.</p>
          </div>
          <div className="observation">
            <span className="observation-mark">03</span>
            <p>Health and family together account for 39% of your category activity.</p>
          </div>
        </article>
      </section>
    </div>
  );
}
