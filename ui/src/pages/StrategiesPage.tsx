import {
  Clock3,
  Scale,
  Target,
  TimerReset,
} from "lucide-react";
import "./StrategiesPage.css";

const strategies = [
  {
    name: "Earliest Deadline First",
    icon: Clock3,
    description:
      "Prioritises tasks according to their deadlines. The task with the earliest deadline is scheduled first.",
    howItWorks: [
      "Earlier deadlines are given priority.",
      "If deadlines are the same, the task with higher importance comes first.",
      "If they are still tied, the shorter task comes first.",
    ],
    bestFor:
      "Useful when you have several deadlines and want to make sure urgent work is handled first.",
  },
  {
    name: "Weighted Focus Mode",
    icon: Target,
    description:
      "Prioritises tasks using a score that considers your selected focus category together with importance, urgency and effort.",
    howItWorks: [
      "Focus category contributes 40% of the score.",
      "Importance contributes 30%.",
      "Urgency contributes 20%.",
      "Effort contributes 10%.",
    ],
    bestFor:
      "Useful when you want to give more attention to a particular area such as Academic, Family, Health or Leisure.",
  },
  {
    name: "Slack Focus",
    icon: TimerReset,
    description:
      "Looks at how much time a task can be delayed while also considering your selected focus category.",
    howItWorks: [
      "Critical tasks with no remaining slack are handled first.",
      "For other tasks, slack contributes 60% of the priority score.",
      "The selected focus category contributes 40%.",
    ],
    bestFor:
      "Useful when you want to identify tasks that are running out of available time while still considering your current focus.",
  },
  {
    name: "Category Balance",
    icon: Scale,
    description:
      "Creates a more balanced schedule by distributing tasks across the different life categories.",
    howItWorks: [
      "Critical tasks are scheduled first.",
      "Remaining tasks are grouped by category.",
      "Tasks are selected using a weighted round-robin approach.",
      "The selected focus category receives more weight than the other categories.",
    ],
    bestFor:
      "Useful when you want to avoid spending the entire day on one category and maintain a more balanced plan.",
  },
];

export default function StrategiesPage() {
  return (
    <div className="strategies-page">
      <header className="strategies-header">
        <span>Scheduling guide</span>
        <h1>About Strategies</h1>
        <p>
          IMTPS provides four scheduling strategies. Each one prioritises your
          tasks differently depending on what you want your schedule to focus on.
        </p>
      </header>

      <section className="strategies-list">
        {strategies.map(({ name, icon: Icon, description, howItWorks, bestFor }) => (
          <article className="strategy-section" key={name}>
            <div className="strategy-title">
              <div className="strategy-icon">
                <Icon size={22} />
              </div>
              <div>
                <h2>{name}</h2>
                <p>{description}</p>
              </div>
            </div>

            <div className="strategy-information">
              <div>
                <h3>How it works</h3>
                <ul>
                  {howItWorks.map((step) => (
                    <li key={step}>{step}</li>
                  ))}
                </ul>
              </div>

              <div className="strategy-best-for">
                <h3>When to use it</h3>
                <p>{bestFor}</p>
              </div>
            </div>
          </article>
        ))}
      </section>
    </div>
  );
}
