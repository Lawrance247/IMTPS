import {
  BarChart3,
  CalendarDays,
  Crosshair,
  FileText,
  LayoutDashboard,
  ListTodo,
  LogOut,
  Settings,
} from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import "./Sidebar.css";

const items = [
  { to: "/today", label: "Today", icon: LayoutDashboard },
  { to: "/tasks", label: "Tasks", icon: ListTodo },
  { to: "/schedule", label: "Schedule", icon: CalendarDays },
  { to: "/focus", label: "Focus", icon: Crosshair },
  { to: "/notes", label: "Notes", icon: FileText },
  { to: "/insights", label: "Insights", icon: BarChart3 },
  { to: "/settings", label: "Settings", icon: Settings },
];

export default function Sidebar() {
  const navigate = useNavigate();

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-mark"><span /><span /></div>
        <div className="sidebar-brand-copy">
          <strong>IMTPS</strong>
          <small>Plan smarter</small>
        </div>
      </div>

      <nav className="sidebar-nav">
        {items.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `sidebar-link ${isActive ? "active" : ""}`
            }
          >
            <Icon size={19} />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-user">
        <div className="sidebar-avatar">LM</div>
        <div className="sidebar-user-copy">
          <strong>Lawrence</strong>
          <small>Student</small>
        </div>
        <button
          className="sidebar-logout"
          onClick={() => navigate("/login")}
          aria-label="Sign out"
        >
          <LogOut size={17} />
        </button>
      </div>
    </aside>
  );
}
