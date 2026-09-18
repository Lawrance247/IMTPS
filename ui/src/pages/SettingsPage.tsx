import {
  Bell,
  Check,
  ChevronRight,
  Lock,
  Moon,
  Palette,
  Save,
  Sun,
  UserRound,
} from "lucide-react";
import { useEffect, useState } from "react";
import "./SettingsPage.css";
import { API_BASE_URL } from "../config";
import { authHeaders } from "../auth";

type Theme = "light" | "dark";
type Strategy = "deadline" | "focus" | "slack" | "balance";
type FocusMinutes = "30" | "45" | "60" | "90";
type SettingsSection = "profile" | "appearance" | "notifications" | "account";

type LoggedInUser = {
  id: string;
  fullName: string;
  email: string;
  createdAt: string;
};

const themes: { id: Theme; label: string; icon: typeof Sun }[] = [
  { id: "light", label: "Light", icon: Sun },
  { id: "dark", label: "Dark", icon: Moon },
];

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

export default function SettingsPage() {
  const currentUser = loadCurrentUser();
  const [theme, setTheme] = useState<Theme>(
    () => (localStorage.getItem("impts-theme") as Theme) || "dark",
  );
  const [saved, setSaved] = useState(false);
  const [activeSection, setActiveSection] = useState<SettingsSection>("profile");
  const [defaultStrategy, setDefaultStrategy] = useState<Strategy>("focus");
  const [defaultFocusMinutes, setDefaultFocusMinutes] = useState<FocusMinutes>("60");
  const [dailyReminder, setDailyReminder] = useState(true);
  const [saveError, setSaveError] = useState("");

  useEffect(() => {
    const root = document.documentElement;
    root.dataset.theme = theme;
    localStorage.setItem("impts-theme", theme);
  }, [theme]);

  useEffect(() => {
    async function loadSettings() {
      try {
        const response = await fetch(`${API_BASE_URL}/settings`, {
          headers: authHeaders(),
        });
        if (!response.ok) return;

        const data = await response.json();
        setTheme(data.theme as Theme);
        setDefaultStrategy(data.defaultStrategy as Strategy);
        setDefaultFocusMinutes(String(data.defaultFocusMinutes) as FocusMinutes);
        setDailyReminder(Boolean(data.dailyReminder));
      } catch {
        // Keep the local defaults if the API is temporarily unavailable.
      }
    }
    loadSettings();
  }, []);

  async function saveSettings() {
    try {
      setSaveError("");
      const response = await fetch(`${API_BASE_URL}/settings`, {
        method: "PUT",
        headers: authHeaders(true),
        body: JSON.stringify({
          theme,
          defaultStrategy,
          defaultFocusMinutes: Number(defaultFocusMinutes),
          dailyReminder,
        }),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => null);
        throw new Error(body?.error ?? "Could not save settings.");
      }

      setSaved(true);
      window.setTimeout(() => setSaved(false), 1800);
    } catch (error) {
      setSaveError(error instanceof Error ? error.message : "Could not save settings.");
    }
  }

  const name = currentUser?.fullName || "User";
  const email = currentUser?.email || "No email available";

  return (
    <div className="settings-page">
      <header className="settings-header">
        <div>
          <span className="settings-eyebrow">Make IMTPS yours</span>
          <h1>Settings</h1>
          <p>Manage your profile, appearance and planning preferences.</p>
        </div>
        <button className="settings-save" onClick={saveSettings}>
          {saved ? <Check size={17} /> : <Save size={17} />}
          {saved ? "Saved" : "Save changes"}
        </button>
      </header>

      {saveError && <p>{saveError}</p>}

      <div className="settings-layout">
        <aside className="settings-nav">
          <button className={activeSection === "profile" ? "active" : ""} onClick={() => setActiveSection("profile")}>
            <UserRound size={17} />Profile<ChevronRight size={15} />
          </button>
          <button className={activeSection === "appearance" ? "active" : ""} onClick={() => setActiveSection("appearance")}>
            <Palette size={17} />Appearance<ChevronRight size={15} />
          </button>
          <button className={activeSection === "notifications" ? "active" : ""} onClick={() => setActiveSection("notifications")}>
            <Bell size={17} />Notifications<ChevronRight size={15} />
          </button>
          <button className={activeSection === "account" ? "active" : ""} onClick={() => setActiveSection("account")}>
            <Lock size={17} />Account<ChevronRight size={15} />
          </button>
        </aside>

        <main className="settings-content">
          {activeSection === "profile" && (
            <>
              <section className="settings-card">
                <div className="settings-card-heading">
                  <div><span>Profile</span><h2>Your details</h2></div>
                  <UserRound size={20} />
                </div>
                <div className="profile-row">
                  <div className="profile-avatar">{initials(name)}</div>
                  <div>
                    <strong>{name}</strong>
                    <span>{email}</span>
                  </div>
                </div>
              </section>

              <section className="settings-card">
                <div className="settings-card-heading">
                  <div><span>Planning</span><h2>Default preferences</h2></div>
                </div>
                <div className="settings-form-grid">
                  <label>
                    <span>Default strategy</span>
                    <select value={defaultStrategy} onChange={(event) => setDefaultStrategy(event.target.value as Strategy)}>
                      <option value="deadline">Earliest Deadline First</option>
                      <option value="focus">Weighted Focus Mode</option>
                      <option value="slack">Slack Focus</option>
                      <option value="balance">Category Balance</option>
                    </select>
                  </label>
                  <label>
                    <span>Default focus session</span>
                    <select value={defaultFocusMinutes} onChange={(event) => setDefaultFocusMinutes(event.target.value as FocusMinutes)}>
                      <option value="30">30 minutes</option>
                      <option value="45">45 minutes</option>
                      <option value="60">60 minutes</option>
                      <option value="90">90 minutes</option>
                    </select>
                  </label>
                </div>
              </section>
            </>
          )}

          {activeSection === "appearance" && (
            <section className="settings-card">
              <div className="settings-card-heading">
                <div><span>Appearance</span><h2>Choose your theme</h2></div>
                <Palette size={20} />
              </div>
              <div className="theme-options">
                {themes.map(({ id, label, icon: Icon }) => (
                  <button key={id} className={theme === id ? "active" : ""} onClick={() => setTheme(id)}>
                    <span><Icon size={21} /></span>
                    <strong>{label}</strong>
                    <small>{id === "light" ? "Bright and clean" : "Easy on the eyes"}</small>
                    {theme === id && <Check className="theme-check" size={16} />}
                  </button>
                ))}
              </div>
            </section>
          )}

          {activeSection === "notifications" && (
            <section className="settings-card compact-settings">
              <div>
                <strong>Daily planning reminder</strong>
                <span>Remind me to review my plan at the start of the day.</span>
              </div>
              <label className="settings-switch">
                <input type="checkbox" checked={dailyReminder} onChange={(event) => setDailyReminder(event.target.checked)} />
                <span />
              </label>
            </section>
          )}

          {activeSection === "account" && (
            <section className="settings-card">
              <div className="settings-card-heading">
                <div><span>Account</span><h2>Account settings</h2></div>
                <Lock size={20} />
              </div>
              <div className="profile-row">
                <div className="profile-avatar">{initials(name)}</div>
                <div>
                  <strong>{name}</strong>
                  <span>{email}</span>
                </div>
              </div>
            </section>
          )}
        </main>
      </div>
    </div>
  );
}
