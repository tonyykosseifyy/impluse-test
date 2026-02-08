import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { SessionExpiringModal } from "../components/SessionExpiringModal";
import { SessionWarningBanner } from "../components/SessionWarningBanner";
import { useIdleSession } from "../hooks/useIdleSession";
import { useState } from "react";

function formatExpiry(value: string | null) {
  if (!value) return "N/A";

  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return value;
  }

  return parsed.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
  });
}

export function DashboardPage() {
  const { auth, logout, ping } = useAuth();
  const navigate = useNavigate();
  const [extendBusy, setExtendBusy] = useState(false);
  const [extendError, setExtendError] = useState<string | null>(null);

  const onAutoLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  const { showBanner, showModal, warningSecondsLeft, modalSecondsLeft, resetIdle } = useIdleSession(true, onAutoLogout);

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  const handleExtend = async () => {
    setExtendBusy(true);
    setExtendError(null);

    try {
      await ping();
      resetIdle();
    } catch (error) {
      setExtendError(error instanceof Error ? error.message : "Unable to extend session.");
    } finally {
      setExtendBusy(false);
    }
  };

  return (
    <main className="page-shell dashboard-shell">
      {showBanner ? <SessionWarningBanner secondsLeft={warningSecondsLeft} /> : null}
      {showModal ? (
        <SessionExpiringModal
          secondsLeft={modalSecondsLeft}
          onExtend={handleExtend}
          busy={extendBusy}
          error={extendError}
        />
      ) : null}

      <section className="card dashboard-card" aria-labelledby="dashboard-title">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Authenticated Area</p>
            <h1 id="dashboard-title">Dashboard</h1>
          </div>
          <button className="danger-button" onClick={handleLogout}>Logout</button>
        </div>

        <div className="stats-grid" aria-label="Session details">
          <article className="stat">
            <p className="stat-label">Authenticated role</p>
            <p className="stat-value">{auth.role ?? "Unknown"}</p>
          </article>
          <article className="stat">
            <p className="stat-label">Access token expiry</p>
            <p className="stat-value">{formatExpiry(auth.expiresAt)}</p>
          </article>
        </div>

        <p className="support-text">
          Stay active to keep your session alive. Idle warnings appear automatically after 2 minutes.
        </p>
      </section>
    </main>
  );
}
