import { createContext, useCallback, useContext, useMemo, useState } from "react";
import { authApi } from "../api/auth/AuthApi";
import { parseRoleFromJwt } from "./jwt";
import type { AuthResponse, AuthState } from "../api/auth/auth.types";

type AuthContextType = {
  auth: AuthState;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  refresh: () => Promise<void>;
  logout: () => Promise<void>;
  ping: () => Promise<void>;
  clearAuth: () => void;
};

const AUTH_STORAGE_KEY = "inpulse_auth";

const defaultState: AuthState = {
  accessToken: null,
  refreshToken: null,
  role: null,
  expiresAt: null
};

const AuthContext = createContext<AuthContextType | null>(null);

function loadAuthState(): AuthState {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) return defaultState;
  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    return defaultState;
  }
}

function mapAuthResponse(data: AuthResponse): AuthState {
  return {
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    role: data.role ?? parseRoleFromJwt(data.accessToken),
    expiresAt: data.expiresIn
  };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [auth, setAuth] = useState<AuthState>(() => loadAuthState());

  const persist = useCallback((next: AuthState) => {
    setAuth(next);
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(next));
  }, []);

  const clearAuth = useCallback(() => {
    setAuth(defaultState);
    localStorage.removeItem(AUTH_STORAGE_KEY);
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      const response = await authApi.login(email, password);
      persist(mapAuthResponse(response));
    },
    [persist]
  );

  const refresh = useCallback(async () => {
    if (!auth.refreshToken) throw new Error("No refresh token found");
    const response = await authApi.refresh(auth.refreshToken);
    persist(mapAuthResponse(response));
  }, [auth.refreshToken, persist]);

  const logout = useCallback(async () => {
    if (auth.refreshToken) {
      try {
        await authApi.logout(auth.refreshToken);
      } finally {
        clearAuth();
      }
      return;
    }

    clearAuth();
  }, [auth.refreshToken, clearAuth]);

  const ping = useCallback(async () => {
    if (!auth.accessToken) throw new Error("No access token found");
    await authApi.ping(auth.accessToken);
  }, [auth.accessToken]);

  const value = useMemo(
    () => ({
      auth,
      isAuthenticated: Boolean(auth.accessToken && auth.refreshToken),
      login,
      refresh,
      logout,
      ping,
      clearAuth
    }),
    [auth, login, refresh, logout, ping, clearAuth]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}
