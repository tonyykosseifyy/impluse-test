import type { ApiError } from "./api.types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

type RequestOptions = {
  method?: "GET" | "PUT" | "POST" | "DELETE";
  body?: unknown;
  token?: string | null;
};

export class ApiClient {
  protected async request<T>(path: string, options: RequestOptions = {}): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: options.method ?? "GET",
      headers: {
        "Content-Type": "application/json",
        ...(options.token ? { Authorization: `Bearer ${options.token}` } : {})
      },
      body: options.body ? JSON.stringify(options.body) : undefined
    });

    if (!response.ok) {
      const fallback: ApiError = { message: "Unexpected request failure" };
      let error = fallback;

      try {
        const parsed = (await response.json()) as ApiError;
        error = parsed.message ? parsed : fallback;
      } catch {
        error = fallback;
      }

      throw new Error(error.message);
    }

    if (response.status === 204) {
      return {} as T;
    }

    return (await response.json()) as T;
  }
}
