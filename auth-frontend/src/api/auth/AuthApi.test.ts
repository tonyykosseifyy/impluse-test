import { describe, expect, it, vi, afterEach } from "vitest";
import { AuthApi } from "./AuthApi";

afterEach(() => {
  vi.restoreAllMocks();
});

describe("AuthApi", () => {
  it("login should call /api/auth/login", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      new Response(
        JSON.stringify({
          accessToken: "a",
          refreshToken: "r",
          tokenType: "Bearer",
          expiresIn: "2030-01-01T00:00:00Z",
          role: "ADMIN"
        }),
        { status: 200 }
      )
    );

    const api = new AuthApi();
    const result = await api.login("admin@inpulse.dev", "ChangeMe123!");

    expect(result.accessToken).toBe("a");
    expect(globalThis.fetch).toHaveBeenCalledWith("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: "admin@inpulse.dev", password: "ChangeMe123!" })
    });
  });

  it("refresh should call /api/auth/refresh", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      new Response(
        JSON.stringify({
          accessToken: "a2",
          refreshToken: "r2",
          tokenType: "Bearer",
          expiresIn: "2030-01-01T00:15:00Z",
          role: "ADMIN"
        }),
        { status: 200 }
      )
    );

    const api = new AuthApi();
    await api.refresh("old-refresh");

    expect(globalThis.fetch).toHaveBeenCalledWith("http://localhost:8080/api/auth/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken: "old-refresh" })
    });
  });

  it("logout should call /api/auth/logout", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(new Response(null, { status: 204 }));

    const api = new AuthApi();
    await api.logout("refresh-token");

    expect(globalThis.fetch).toHaveBeenCalledWith("http://localhost:8080/api/auth/logout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken: "refresh-token" })
    });
  });

  it("ping should send bearer token", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      new Response(JSON.stringify({ status: "ok", time: "2026-01-01T00:00:00Z" }), { status: 200 })
    );

    const api = new AuthApi();
    await api.ping("access-token");

    expect(globalThis.fetch).toHaveBeenCalledWith("http://localhost:8080/api/auth/ping", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer access-token"
      },
      body: undefined
    });
  });
});
