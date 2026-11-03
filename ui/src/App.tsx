import { Navigate, Outlet, Route, Routes } from "react-router-dom";
import AppShell from "./components/layout/AppShell";
import StrategiesPage from "./pages/StrategiesPage";
import FocusPage from "./pages/FocusPage";
import InsightsPage from "./pages/InsightsPage";
import LoginPage from "./pages/LoginPage";
import NotesPage from "./pages/NotesPage";
import RegisterPage from "./pages/RegisterPage";
import SchedulePage from "./pages/SchedulePage";
import SettingsPage from "./pages/SettingsPage";
import TasksPage from "./pages/TasksPage";
import TodayPage from "./pages/TodayPage";
import WelcomePage from "./pages/WelcomePage";

function RequireLogin() {
  const currentUser = localStorage.getItem("imtps.currentUser");
  const token = localStorage.getItem("imtps.authToken");
  return currentUser && token ? <Outlet /> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<RequireLogin />}>
        <Route element={<AppShell />}>
          <Route path="/today" element={<TodayPage />} />
          <Route path="/tasks" element={<TasksPage />} />
          <Route path="/schedule" element={<SchedulePage />} />
          <Route path="/focus" element={<FocusPage />} />
          <Route path="/notes" element={<NotesPage />} />
          <Route path="/insights" element={<InsightsPage />} />
          <Route path="/strategies" element={<StrategiesPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
