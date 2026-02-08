export function SessionWarningBanner({ secondsLeft }: { secondsLeft: number }) {
  return (
    <div className="session-banner" role="status" aria-live="polite">
      <strong>Session warning:</strong> inactivity detected. Session expiring modal in {secondsLeft}s.
    </div>
  );
}
