import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import WelcomePage from "./pages/WelcomePage";

function ComingSoon({ title }: { title: string }) {
  return (
    <main className="page">
      <div className="page-content">
        <h1>{title}</h1>
        <p>This screen is being built next.</p>
      </div>
    </main>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/today" element={<ComingSoon title="Today" />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
