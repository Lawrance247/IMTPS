import {
  Bell,
  Check,
  ChevronRight,
  Laptop,
  Lock,
  Moon,
  Palette,
  Save,
  Sun,
  UserRound,
} from "lucide-react";
import { useEffect, useState } from "react";
import "./SettingsPage.css";

type Theme = "light" | "dark" | "system";

const themes: { id: Theme; label: string; icon: typeof Sun }[] = [
  { id: "light", label: "Light", icon: Sun },
  { id: "dark", label: "Dark", icon: Moon },
  { id: "system", label: "System", icon: Laptop },
];

export default function SettingsPage() {
  const [theme, setTheme] = useState<Theme>(
    () => (localStorage.getItem("impts-theme") as Theme) || "dark",
  );
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    const root = document.documentElement;

    const applyTheme = () => {
      const resolved =
        theme === "system"
          ? window.matchMedia("(prefers-color-scheme: dark)").matches
            ? "dark"
            : "light"
          : theme;

      root.dataset.theme = resolved;
    };

    applyTheme();
    localStorage.setItem("impts-theme", theme);

    const media = window.matchMedia("(prefers-color-scheme: dark)");
    media.addEventListener("change", applyTheme);
    return () => media.removeEventListener("change", applyTheme);
  }, [theme]);

  function saveSettings() {
    setSaved(true);
    window.setTimeout(() => setSaved(false), 1800);
  }

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

      <div className="settings-layout">
        <aside className="settings-nav">
          <button className="active"><UserRound size={17} />Profile<ChevronRight size={15} /></button>
          <button><Palette size={17} />Appearance<ChevronRight size={15} /></button>
          <button><Bell size={17} />Notifications<ChevronRight size={15} /></button>
          <button><Lock size={17} />Account<ChevronRight size={15} /></button>
        </aside>

        <main className="settings-content">
          <section className="settings-card">
            <div className="settings-card-heading">
              <div>
                <span>Profile</span>
                <h2>Your details</h2>
              </div>
              <UserRound size={20} />
            </div>

            <div className="profile-row">
              <div className="profile-avatar">L</div>
              <div>
                <strong>Lawrence</strong>
                <span>IMTPS user</span>
              </div>
              <button>Change photo</button>
            </div>

            <div className="settings-form-grid">
              <label>
                <span>Display name</span>
                <input defaultValue="Lawrence" />
              </label>
              <label>
                <span>Email</span>
                <input type="email" placeholder="you@example.com" />
              </label>
            </div>
          </section>

          <section className="settings-card">
            <div className="settings-card-heading">
              <div>
                <span>Appearance</span>
                <h2>Choose your theme</h2>
              </div>
              <Palette size={20} />
            </div>

            <div className="theme-options">
              {themes.map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  className={theme === id ? "active" : ""}
                  onClick={() => setTheme(id)}
                >
                  <span><Icon size={21} /></span>
                  <strong>{label}</strong>
                  <small>
                    {id === "light" && "Bright and clean"}
                    {id === "dark" && "Easy on the eyes"}
                    {id === "system" && "Match your device"}
                  </small>
                  {theme === id && <Check className="theme-check" size={16} />}
                </button>
              ))}
            </div>
          </section>

          <section className="settings-card">
            <div className="settings-card-heading">
              <div>
                <span>Planning</span>
                <h2>Default preferences</h2>
              </div>
            </div>

            <div className="settings-form-grid">
              <label>
                <span>Default strategy</span>
                <select defaultValue="Focus">
                  <option>Deadline First</option>
                  <option>Focus</option>
                  <option>Category Balance</option>
                </select>
              </label>
              <label>
                <span>Default focus session</span>
                <select defaultValue="60">
                  <option value="30">30 minutes</option>
                  <option value="45">45 minutes</option>
                  <option value="60">60 minutes</option>
                  <option value="90">90 minutes</option>
                </select>
              </label>
            </div>
          </section>

          <section className="settings-card compact-settings">
            <div>
              <strong>Daily planning reminder</strong>
              <span>Remind me to review my plan at the start of the day.</span>
            </div>
            <label className="settings-switch">
              <input type="checkbox" defaultChecked />
              <span />
            </label>
          </section>
        </main>
      </div>
    </div>
  );
}
