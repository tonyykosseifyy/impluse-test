import { describe, expect, it, vi, afterEach } from "vitest";
import { ApiClient } from "./ApiClient";

class TestApiClient extends ApiClient {
  call<T>(path: string, options?: { method?: "GET" | "PUT" | "POST" | "DELETE"; body?: unknown; token?: string | null }) {
    return this.request<T>(path, options);
  }
}

afterEach(() => {
  vi.restoreAllMocks();
});

describe("ApiClient", () => {
  it("should send JSON request and parse response", async () => {
    const fetchSpy = vi.spyOn(globalThis, "fetch").mockResolvedValue(
      new Response(JSON.stringify({ ok: true }), { status: 200 })
    );

    const client = new TestApiClient();
    const data = await client.call<{ ok: boolean }>("/api/test", {
      method: "POST",
      body: { name: "inpulse" },
      token: "abc"
    });

    expect(data).toEqual({ ok: true });
    expect(fetchSpy).toHaveBeenCalledOnce();
    expect(fetchSpy).toHaveBeenCalledWith("http://localhost:8080/api/test", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer abc"
      },
      body: JSON.stringify({ name: "inpulse" })
    });
  });

  it("should return empty object on 204", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(new Response(null, { status: 204 }));

    const client = new TestApiClient();
    const result = await client.call<Record<string, never>>("/api/no-content");

    expect(result).toEqual({});
  });

  it("should throw backend error message", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      new Response(JSON.stringify({ message: "Invalid credentials" }), { status: 401 })
    );

    const client = new TestApiClient();

    await expect(client.call("/api/auth/login")).rejects.toThrow("Invalid credentials");
  });

  it("should throw fallback message when error payload is invalid", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(new Response("not-json", { status: 500 }));

    const client = new TestApiClient();

    await expect(client.call("/api/fail")).rejects.toThrow("Unexpected request failure");
  });
});
