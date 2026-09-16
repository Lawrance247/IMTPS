import { Eye, EyeOff, LockKeyhole, Mail } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    navigate("/today");
  }

  return (
    <main className="login-page">
      <section className="login-visual">
        <div className="login-visual-shade" />

        <Link to="/" className="login-brand">
          <div className="login-mark">
            <span />
            <span />
          </div>
          <div>
            <strong>IMTPS</strong>
            <small>Intelligent Task Prioritisation System</small>
          </div>
        </Link>

        <div className="login-message">
          <span>Balance what matters</span>
          <h1>Plan your day without losing sight of your life.</h1>
          <p>
            Academic goals matter, but so do family, health and time to recharge.
            IMTPS helps you make room for all four.
          </p>
        </div>

        <div className="login-areas" aria-label="IMTPS life areas">
          <span className="login-area academic">Academic</span>
          <span className="login-area family">Family</span>
          <span className="login-area health">Health</span>
          <span className="login-area leisure">Leisure</span>
        </div>
      </section>

      <section className="login-panel">
        <div className="login-form-wrap">
          <div className="login-heading">
            <span className="login-kicker">Welcome back</span>
            <h2>Good to see you again.</h2>
            <p>Sign in to continue planning your day.</p>
          </div>

          <form className="login-form" onSubmit={handleSubmit}>
            <label className="login-field">
              <span>Email</span>
              <div className="login-input-wrap">
                <Mail size={18} />
                <input
                  type="email"
                  name="email"
                  placeholder="you@uct.ac.za"
                  autoComplete="email"
                  required
                />
              </div>
            </label>

            <label className="login-field">
              <span>Password</span>
              <div className="login-input-wrap">
                <LockKeyhole size={18} />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword((current) => !current)}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </label>

            <div className="login-options">
              <label className="remember-me">
                <input type="checkbox" />
                <span>Remember me</span>
              </label>

              <button type="button" className="forgot-password">
                Forgot password?
              </button>
            </div>

            <button className="login-submit" type="submit">
              Sign In
            </button>
          </form>

          <div className="login-divider">
            <span />
            <p>New to IMTPS?</p>
            <span />
          </div>

          <Link className="create-account-link" to="/register">
            Create your account
          </Link>

          <p className="login-note">
            Your tasks, notes and preferences stay connected to your account.
          </p>
        </div>
      </section>
    </main>
  );
}
