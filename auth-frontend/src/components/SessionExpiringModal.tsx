type SessionExpiringModalProps = {
  secondsLeft: number;
  onExtend: () => Promise<void>;
  busy: boolean;
  error: string | null;
};

export function SessionExpiringModal({ secondsLeft, onExtend, busy, error }: SessionExpiringModalProps) {
  return (
    <div className="modal-backdrop" role="presentation">
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="session-expiring-title">
        <h2 id="session-expiring-title">Session expiring</h2>
        <p>Your session will end in {secondsLeft}s due to inactivity.</p>
        {error ? <p className="error-text" role="alert">{error}</p> : null}
        <button onClick={onExtend} disabled={busy}>
          {busy ? "Extending..." : "Extend session"}
        </button>
      </div>
    </div>
  );
}
