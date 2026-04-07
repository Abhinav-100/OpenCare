import { LoginResponse } from "@/shared/types/auth";

/**
 * Decode JWT payload (client-side, without verification)
 */
function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;
    const payload = atob(parts[1]);
    return JSON.parse(payload);
  } catch {
    return null;
  }
}

/**
 * Check if user has admin role from the access token
 */
// Auth flow: This file handles session/token actions used by login/logout/refresh flows.
export function hasAdminRole(accessToken: string): boolean {
  const payload = decodeJwtPayload(accessToken);
  if (!payload) return false;

  // Check realm_access.roles for admin roles
  const realmAccess = payload.realm_access as { roles?: string[] } | undefined;
  const roles = realmAccess?.roles ?? [];

  const adminRoles = ["admin", "superadmin", "ADMIN", "SUPERADMIN", "super-admin"];
  return roles.some((role) => adminRoles.includes(role));
}

/**
 * Check if current session has admin role
 */
export function isCurrentUserAdmin(): boolean {
  const session = getUserSession();
  if (!session?.access_token) return false;
  return hasAdminRole(session.access_token);
}

/**
 * User info extracted from JWT
 */
export interface JwtUserInfo {
  name?: string;
  givenName?: string;
  familyName?: string;
  email?: string;
  username?: string;
}

/**
 * Get user info from current session's JWT token
 */
export function getUserInfo(): JwtUserInfo | null {
  const session = getUserSession();
  if (!session?.access_token) return null;

  const payload = decodeJwtPayload(session.access_token);
  if (!payload) return null;

  return {
    name: payload.name as string | undefined,
    givenName: payload.given_name as string | undefined,
    familyName: payload.family_name as string | undefined,
    email: payload.email as string | undefined,
    username: payload.preferred_username as string | undefined,
  };
}

/**
 * Client-side function to get user session from localStorage
 */
export function getUserSession(): LoginResponse | null {
  if (typeof window === "undefined") return null;

  try {
    const sessionData = localStorage.getItem("user_session");
    return sessionData ? JSON.parse(sessionData) : null;
  } catch {
    return null;
  }
}

/**
 * Client-side function to save user session to localStorage
 */
export function saveUserSession(response: LoginResponse): void {
  if (typeof window === "undefined") return;

  localStorage.setItem("user_session", JSON.stringify(response));
}

/**
 * Client-side function to clear user session from localStorage
 */
export function clearUserSession(): void {
  if (typeof window === "undefined") return;

  localStorage.removeItem("user_session");
}

/**
 * Client-side function to get authorization header
 */
export function getAuthHeader(): { Authorization?: string } {
  const session = getUserSession();

  if (session?.access_token && session?.token_type) {
    return {
      Authorization: `${session.token_type} ${session.access_token}`,
    };
  }

  return {};
}

/**
 * Client-side logout function
 */
export function clientLogout(): void {
  // Clear localStorage session
  clearUserSession();

  // Redirect to login page
  if (typeof window !== "undefined") {
    window.location.href = "/login";
  }
}
