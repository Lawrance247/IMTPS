import { Eye, EyeOff, LockKeyhole, Mail, UserRound } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./RegisterPage.css";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const form = new FormData(event.currentTarget);
    const password = String(form.get("password") ?? "");
    const confirmPassword = String(form.get("confirmPassword") ?? "");

    if (password !== confirmPassword) {
      setError("The passwords do not match.");
      return;
    }

    setError("");
    navigate("/today");
  }

  return (
    <main className="register-page">
      <section className="register-visual">
        <div className="register-visual-shade" />

        <Link to="/" className="register-brand">
          <div className="register-mark">
            <span />
            <span />
          </div>
          <div>
            <strong>IMTPS</strong>
            <small>Intelligent Task Prioritisation System</small>
          </div>
        </Link>

        <div className="register-message">
          <span>Start with balance</span>
          <h1>Build a routine around the life you actually live.</h1>
          <p>
            Bring your academic work, family time, health and leisure into one
            plan, then let IMTPS help you decide what deserves attention next.
          </p>
        </div>

        <p className="register-quote">
          Plan smarter. Prioritise better. Live a more balanced life.
        </p>
      </section>

      <section className="register-panel">
        <div className="register-form-wrap">
          <div className="register-heading">
            <span className="register-kicker">Join IMTPS</span>
            <h2>Create your account.</h2>
            <p>A few details and you're ready to start planning.</p>
          </div>

          <form className="register-form" onSubmit={handleSubmit}>
            <label className="register-field">
              <span>Full name</span>
              <div className="register-input-wrap">
                <UserRound size={18} />
                <input
                  type="text"
                  name="fullName"
                  placeholder="e.g. Lawrence Mavimbela"
                  autoComplete="name"
                  required
                />
              </div>
            </label>

            <label className="register-field">
              <span>Email</span>
              <div className="register-input-wrap">
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

            <label className="register-field">
              <span>Password</span>
              <div className="register-input-wrap">
                <LockKeyhole size={18} />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="Create a password"
                  autoComplete="new-password"
                  minLength={6}
                  required
                />
                <button
                  type="button"
                  className="register-password-toggle"
                  onClick={() => setShowPassword((current) => !current)}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </label>

            <label className="register-field">
              <span>Confirm password</span>
              <div className="register-input-wrap">
                <LockKeyhole size={18} />
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  name="confirmPassword"
                  placeholder="Enter your password again"
                  autoComplete="new-password"
                  minLength={6}
                  required
                />
                <button
                  type="button"
                  className="register-password-toggle"
                  onClick={() => setShowConfirmPassword((current) => !current)}
                  aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                >
                  {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </label>

            {error && <p className="register-error">{error}</p>}

            <button className="register-submit" type="submit">
              Create Account
            </button>
          </form>

          <p className="register-signin">
            Already have an account? <Link to="/login">Sign in</Link>
          </p>

          <p className="register-note">
            Your account will later connect to the IMTPS backend and database.
          </p>
        </div>
      </section>
    </main>
  );
}
