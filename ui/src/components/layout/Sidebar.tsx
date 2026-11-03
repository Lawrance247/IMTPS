import {
  BarChart3,
  CalendarDays,
  Crosshair,
  FileText,
  LayoutDashboard,
  BookOpen,
  ListTodo,
  LogOut,
  Settings,
} from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../../config";
import { authHeaders, clearAuth } from "../../auth";
import "./Sidebar.css";

type LoggedInUser = {
  id: string;
  fullName: string;
  email: string;
  createdAt: string;
};

function loadCurrentUser(): LoggedInUser | null {
  const saved = localStorage.getItem("imtps.currentUser");
  if (!saved) return null;

  try {
    return JSON.parse(saved) as LoggedInUser;
  } catch {
    return null;
  }
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "?";
  return parts.slice(0, 2).map((part) => part[0].toUpperCase()).join("");
}

const items = [
  { to: "/today", label: "Today", icon: LayoutDashboard },
  { to: "/tasks", label: "Tasks", icon: ListTodo },
  { to: "/schedule", label: "Schedule", icon: CalendarDays },
  { to: "/focus", label: "Focus", icon: Crosshair },
  { to: "/notes", label: "Notes", icon: FileText },
  { to: "/insights", label: "Insights", icon: BarChart3 },
  { to: "/strategies", label: "About Strategies", icon: BookOpen },
  { to: "/settings", label: "Settings", icon: Settings },
];

export default function Sidebar() {
  const navigate = useNavigate();
  const currentUser = loadCurrentUser();
  const displayName = currentUser?.fullName || "User";

  async function signOut() {
    try {
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: "POST",
        headers: authHeaders(),
      });
    } catch {
      // The local session is cleared either way.
    }

    clearAuth();
    navigate("/login", { replace: true });
  }

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
        <div className="sidebar-avatar">{initials(displayName)}</div>
        <div className="sidebar-user-copy">
          <strong>{displayName}</strong>
          <small>{currentUser?.email || "Signed in"}</small>
        </div>
        <button
          className="sidebar-logout"
          onClick={signOut}
          aria-label="Sign out"
        >
          <LogOut size={17} />
        </button>
      </div>
    </aside>
  );
}
