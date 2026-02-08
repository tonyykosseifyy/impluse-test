import { useEffect, useMemo, useRef, useState } from "react";

const WARNING_MS = 2 * 60 * 1000;
const MODAL_MS = 3 * 60 * 1000;
const MODAL_DURATION_MS = 30 * 1000;
const MODAL_END_MS = MODAL_MS + MODAL_DURATION_MS;
const AUTO_LOGOUT_MS = 5 * 60 * 1000;

export function useIdleSession(enabled: boolean, onAutoLogout: () => Promise<void>) {
  const lastActivityRef = useRef<number>(Date.now());
  const [idleMs, setIdleMs] = useState(0);

  useEffect(() => {
    if (!enabled) return;

    const markActivity = () => {
      const currentIdleMs = Date.now() - lastActivityRef.current;
      if (currentIdleMs >= MODAL_MS && currentIdleMs < MODAL_END_MS) {
        return;
      }
      lastActivityRef.current = Date.now();
      setIdleMs(0);
    };

    const events: Array<keyof WindowEventMap> = ["mousemove", "keydown", "mousedown", "touchstart"];
    events.forEach((eventName) => window.addEventListener(eventName, markActivity));

    const timer = window.setInterval(() => {
      setIdleMs(Date.now() - lastActivityRef.current);
    }, 1000);

    return () => {
      events.forEach((eventName) => window.removeEventListener(eventName, markActivity));
      window.clearInterval(timer);
    };
  }, [enabled]);

  useEffect(() => {
    if (!enabled) return;
    if (idleMs < AUTO_LOGOUT_MS) return;

    void onAutoLogout();
  }, [enabled, idleMs, onAutoLogout]);

  const state = useMemo(() => {
    const showBanner = idleMs >= WARNING_MS && idleMs < MODAL_MS;
    const showModal = idleMs >= MODAL_MS && idleMs < MODAL_END_MS;
    const warningSecondsLeft = Math.max(0, Math.ceil((MODAL_MS - idleMs) / 1000));
    const modalSecondsLeft = Math.max(0, Math.ceil((MODAL_END_MS - idleMs) / 1000));

    return { showBanner, showModal, warningSecondsLeft, modalSecondsLeft };
  }, [idleMs]);

  const resetIdle = () => {
    lastActivityRef.current = Date.now();
    setIdleMs(0);
  };

  return { ...state, resetIdle };
}
