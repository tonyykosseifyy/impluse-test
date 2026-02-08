export type Role = "USER" | "ADMIN";

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: string;
  role: Role;
};

export type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
  role: Role | null;
  expiresAt: string | null;
};
