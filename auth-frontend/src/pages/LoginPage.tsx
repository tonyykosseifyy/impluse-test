import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("admin@inpulse.dev");
  const [password, setPassword] = useState("ChangeMe123!");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await login(email, password);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed. Please retry.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="page-shell login-shell">
      <section className="auth-layout" aria-label="Authentication">
        <article className="hero-panel">
          <p className="eyebrow">Inpulse Security Gateway</p>
          <h1 id="login-title">Sign in</h1>
          <p className="subtitle">Access your dashboard and monitor session security controls in real time.</p>
          <ul className="feature-list" aria-label="Security highlights">
            <li>15-minute access tokens</li>
            <li>Refresh token rotation</li>
            <li>Idle timeout enforcement</li>
          </ul>
        </article>

        <section className="card auth-card" aria-labelledby="login-title">
          <form onSubmit={handleSubmit} className="form">
            <div className="field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                required
                autoComplete="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>

            <div className="field">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                required
                autoComplete="current-password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>

            {error ? <p className="error-text" role="alert">{error}</p> : null}

            <button type="submit" disabled={submitting}>
              {submitting ? "Signing in..." : "Sign in"}
            </button>
          </form>
        </section>
      </section>
    </main>
  );
}
