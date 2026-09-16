import { ArrowRight, Gamepad2, GraduationCap, HeartPulse, Users } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./WelcomePage.css";

const areas = [
  {
    name: "Academic",
    description: "Learn. Achieve. Grow.",
    icon: GraduationCap,
    className: "academic",
  },
  {
    name: "Family",
    description: "Stay connected.",
    icon: Users,
    className: "family",
  },
  {
    name: "Health",
    description: "Be well. Stay strong.",
    icon: HeartPulse,
    className: "health",
  },
  {
    name: "Leisure",
    description: "Recharge. Enjoy.",
    icon: Gamepad2,
    className: "leisure",
  },
];

export default function WelcomePage() {
  const navigate = useNavigate();

  return (
    <main className="welcome-page">
      <div className="welcome-overlay" />

      <section className="welcome-content">
        <div className="welcome-brand">
          <div className="welcome-mark">
            <span />
            <span />
          </div>
          <div>
            <strong>IMTPS</strong>
            <small>Intelligent Task Prioritisation System</small>
          </div>
        </div>

        <div className="welcome-copy">
          <span className="welcome-eyebrow">Plan smarter. Prioritise better.</span>
          <h1>Make space for what matters.</h1>
          <p>
            A smarter way to organise your tasks, choose what deserves your
            attention and build a more balanced day.
          </p>

          <button className="welcome-cta" onClick={() => navigate("/login")}>
            Get Started
            <ArrowRight size={19} strokeWidth={2.2} />
          </button>
        </div>

        <div className="welcome-areas">
          {areas.map(({ name, description, icon: Icon, className }) => (
            <article className={`welcome-area ${className}`} key={name}>
              <div className="welcome-area-icon">
                <Icon size={22} strokeWidth={2} />
              </div>
              <div>
                <strong>{name}</strong>
                <span>{description}</span>
              </div>
            </article>
          ))}
        </div>

        <footer className="welcome-footer">
          <span>Balance today.</span>
          <span className="welcome-footer-dot" />
          <span>A brighter tomorrow.</span>
        </footer>
      </section>
    </main>
  );
}
