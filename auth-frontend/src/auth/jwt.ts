import type { Role } from "../api/auth/auth.types";

export function parseRoleFromJwt(token: string): Role | null {
  try {
    const payload = token.split(".")[1];
    if (!payload) return null;
    const decoded = JSON.parse(atob(payload));
    return (decoded.role as Role) ?? null;
  } catch {
    return null;
  }
}
