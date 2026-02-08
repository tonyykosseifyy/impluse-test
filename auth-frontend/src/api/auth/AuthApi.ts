import { ApiClient } from "../api-client/ApiClient";
import type { AuthResponse } from "./auth.types";

export class AuthApi extends ApiClient {
  login(email: string, password: string) {
    return this.request<AuthResponse>("/api/auth/login", {
      method: "POST",
      body: { email, password }
    });
  }

  refresh(refreshToken: string) {
    return this.request<AuthResponse>("/api/auth/refresh", {
      method: "POST",
      body: { refreshToken }
    });
  }

  logout(refreshToken: string) {
    return this.request<void>("/api/auth/logout", {
      method: "POST",
      body: { refreshToken }
    });
  }

  ping(token: string) {
    return this.request<{ status: string; time: string }>("/api/auth/ping", {
      method: "POST",
      token
    });
  }
}

export const authApi = new AuthApi();
